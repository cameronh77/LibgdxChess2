package io.github.chess_sequel.engine.section;

import io.github.chess_sequel.engine.jsonTypes.GuaranteedRoomConfig;
import io.github.chess_sequel.engine.jsonTypes.RoomPoolEntry;
import io.github.chess_sequel.engine.jsonTypes.SectionConfig;

import java.util.*;

public class SectionLayoutGenerator {

    public static SectionLayout generate(SectionConfig config) {
        Random rng = new Random();
        int w = config.gridWidth;
        int h = config.gridHeight;
        RoomNode[][] grid = new RoomNode[w][h];
        int target = config.targetRooms > 0 ? config.targetRooms : 8;

        // 1. Spawn at a random grid position
        int spawnX = rng.nextInt(w);
        int spawnY = rng.nextInt(h);
        grid[spawnX][spawnY] = new RoomNode(spawnX, spawnY, config.sectionId, "combat");

        // 2. Isaac-style random walk with branching until target room count.
        //    Frontier rooms are never removed after expanding — branches can regrow from them.
        List<int[]> frontier = new ArrayList<>();
        frontier.add(new int[]{spawnX, spawnY});
        int roomCount = 1;

        int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

        while (roomCount < target && !frontier.isEmpty()) {
            int fi = rng.nextInt(frontier.size());
            int[] pos = frontier.get(fi);

            List<int[]> candidates = new ArrayList<>();
            for (int[] d : DIRS) {
                int nx = pos[0] + d[0], ny = pos[1] + d[1];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h && grid[nx][ny] == null) {
                    candidates.add(new int[]{nx, ny});
                }
            }

            if (candidates.isEmpty()) {
                frontier.remove(fi);
                continue;
            }

            int[] next = candidates.get(rng.nextInt(candidates.size()));
            grid[next[0]][next[1]] = new RoomNode(next[0], next[1], config.sectionId, "combat");
            frontier.add(next);
            roomCount++;
        }

        // 3. Extend walk to guarantee enough dead-ends for terminal room designation.
        //    We count all guaranteed rooms pessimistically (assume chance rooms all place).
        //    We only place stubs where the parent already has ≥ 2 neighbours, so each
        //    placement is a guaranteed net +1 dead-end (parent was never a dead-end).
        int terminalsNeeded = config.guaranteedRooms != null ? config.guaranteedRooms.size() : 0;
        while (countDeadEnds(grid, spawnX, spawnY, w, h) < terminalsNeeded) {
            List<int[]> slots = findNetPositiveDeadEndSlots(grid, w, h);
            if (slots.isEmpty()) break; // No beneficial placement possible — grid too constrained
            int[] slot = slots.get(rng.nextInt(slots.size()));
            grid[slot[0]][slot[1]] = new RoomNode(slot[0], slot[1], config.sectionId, "combat");
            roomCount++;
        }

        // 4. BFS distances from spawn
        int[][] dist = bfsDistances(grid, spawnX, spawnY, w, h);

        // 5. Dead-ends = rooms with exactly 1 filled neighbour, excluding spawn.
        //    These are naturally single-door rooms — ideal for terminal placements.
        List<RoomNode> deadEnds = new ArrayList<>();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (grid[x][y] == null || (x == spawnX && y == spawnY)) continue;
                if (filledNeighbourCount(grid, x, y, w, h) == 1) deadEnds.add(grid[x][y]);
            }
        }
        deadEnds.sort((a, b) -> dist[b.gridX][b.gridY] - dist[a.gridX][a.gridY]);

        // 6. Designate terminal rooms from dead-ends in distance order.
        //    Boss = farthest. Others = next. Fallback to farthest unassigned room if needed.
        int deadEndIdx = 0;
        RoomNode bossRoom = null;

        if (!deadEnds.isEmpty()) {
            bossRoom = deadEnds.get(deadEndIdx++);
            bossRoom.type = "boss";
        }

        if (config.guaranteedRooms != null) {
            for (GuaranteedRoomConfig grc : config.guaranteedRooms) {
                if ("boss".equals(grc.type)) continue;
                if (!grc.guaranteed && rng.nextFloat() >= grc.chance) continue;
                RoomNode slot = deadEndIdx < deadEnds.size()
                        ? deadEnds.get(deadEndIdx++)
                        : findFallbackRoom(grid, dist, spawnX, spawnY, w, h);
                if (slot != null) slot.type = grc.type;
            }
        }

        // 7. Fill remaining combat rooms from the weighted pool
        Map<String, Integer> typeCounts = new HashMap<>();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                if (grid[x][y] != null) typeCounts.merge(grid[x][y].type, 1, Integer::sum);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (grid[x][y] == null || !"combat".equals(grid[x][y].type)) continue;
                if (x == spawnX && y == spawnY) continue;
                String picked = pickFromPool(config.roomPool, typeCounts, rng);
                if (picked != null && !"combat".equals(picked)) {
                    typeCounts.merge("combat", -1, Integer::sum);
                    grid[x][y].type = picked;
                    typeCounts.merge(picked, 1, Integer::sum);
                }
            }
        }

        // 8. Add doors between all adjacent filled rooms.
        //    Dead-end rooms receive exactly 1 door — no trim step required.
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (grid[x][y] == null) continue;
                if (x + 1 < w && grid[x+1][y] != null) {
                    grid[x][y].doors.add(Direction.EAST);
                    grid[x+1][y].doors.add(Direction.WEST);
                }
                if (y + 1 < h && grid[x][y+1] != null) {
                    grid[x][y].doors.add(Direction.NORTH);
                    grid[x][y+1].doors.add(Direction.SOUTH);
                }
            }
        }

        // 9. Spawn type and fog of war
        RoomNode spawn = grid[spawnX][spawnY];
        spawn.type = config.spawnRoomType;
        spawn.revealed = true;
        spawn.visited = true;
        for (int[] d : DIRS) {
            int nx = spawnX + d[0], ny = spawnY + d[1];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h && grid[nx][ny] != null) {
                grid[nx][ny].revealed = true;
            }
        }

        SectionLayout layout = new SectionLayout();
        layout.sectionId = config.sectionId;
        layout.grid = grid;
        layout.spawnRoom = spawn;
        layout.bossRoom = bossRoom;
        layout.currentRoom = spawn;
        return layout;
    }

    // -------------------------------------------------------------------------

    /** Counts rooms with exactly 1 filled neighbour, excluding spawn. */
    private static int countDeadEnds(RoomNode[][] grid, int spawnX, int spawnY, int w, int h) {
        int count = 0;
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                if (grid[x][y] != null && !(x == spawnX && y == spawnY)
                        && filledNeighbourCount(grid, x, y, w, h) == 1)
                    count++;
        return count;
    }

    /**
     * Returns empty cells that would become dead-ends when placed AND whose only
     * filled neighbour already has ≥ 2 neighbours. Each such placement is a net
     * +1 dead-end: the parent was not a dead-end and won't become one.
     */
    private static List<int[]> findNetPositiveDeadEndSlots(RoomNode[][] grid, int w, int h) {
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        List<int[]> slots = new ArrayList<>();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (grid[x][y] != null) continue;
                List<int[]> filledNeighbours = new ArrayList<>();
                for (int[] d : dirs) {
                    int nx = x + d[0], ny = y + d[1];
                    if (nx >= 0 && nx < w && ny >= 0 && ny < h && grid[nx][ny] != null)
                        filledNeighbours.add(new int[]{nx, ny});
                }
                if (filledNeighbours.size() == 1) {
                    int px = filledNeighbours.get(0)[0], py = filledNeighbours.get(0)[1];
                    if (filledNeighbourCount(grid, px, py, w, h) >= 2) slots.add(new int[]{x, y});
                }
            }
        }
        return slots;
    }

    private static int filledNeighbourCount(RoomNode[][] grid, int x, int y, int w, int h) {
        int count = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs) {
            int nx = x + d[0], ny = y + d[1];
            if (nx >= 0 && nx < w && ny >= 0 && ny < h && grid[nx][ny] != null) count++;
        }
        return count;
    }

    private static RoomNode findFallbackRoom(RoomNode[][] grid, int[][] dist,
                                             int spawnX, int spawnY, int w, int h) {
        RoomNode best = null;
        int bestDist = -1;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (grid[x][y] == null || (x == spawnX && y == spawnY)) continue;
                if (!"combat".equals(grid[x][y].type)) continue;
                if (dist[x][y] > bestDist) { bestDist = dist[x][y]; best = grid[x][y]; }
            }
        }
        return best;
    }

    private static int[][] bfsDistances(RoomNode[][] grid, int sx, int sy, int w, int h) {
        int[][] dist = new int[w][h];
        for (int[] row : dist) Arrays.fill(row, -1);
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{sx, sy});
        dist[sx][sy] = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!q.isEmpty()) {
            int[] pos = q.poll();
            for (int[] d : dirs) {
                int nx = pos[0] + d[0], ny = pos[1] + d[1];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h
                        && grid[nx][ny] != null && dist[nx][ny] == -1) {
                    dist[nx][ny] = dist[pos[0]][pos[1]] + 1;
                    q.add(new int[]{nx, ny});
                }
            }
        }
        return dist;
    }

    private static String pickFromPool(ArrayList<RoomPoolEntry> pool,
                                       Map<String, Integer> counts, Random rng) {
        int total = 0;
        for (RoomPoolEntry e : pool) {
            if (counts.getOrDefault(e.type, 0) < e.max) total += e.weight;
        }
        if (total == 0) return null;
        int roll = rng.nextInt(total);
        int cum = 0;
        for (RoomPoolEntry e : pool) {
            if (counts.getOrDefault(e.type, 0) >= e.max) continue;
            cum += e.weight;
            if (roll < cum) return e.type;
        }
        return null;
    }
}
