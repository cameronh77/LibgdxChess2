package io.github.chess_sequel.gui.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.chess_sequel.engine.GameRun;
import io.github.chess_sequel.engine.section.Direction;
import io.github.chess_sequel.engine.section.RoomNode;
import io.github.chess_sequel.engine.section.SectionLayout;

public class MapOverlay extends Actor {

    private final GameRun gameRun;
    private final ShapeRenderer shapes;
    private final BitmapFont font;

    private static final float ROOM_W      = 32f;
    private static final float ROOM_H      = 26f;
    private static final float CELL_W      = 54f;
    private static final float CELL_H      = 44f;
    private static final float LINE_W      = 3f;
    private static final float BORDER      = 1.5f;
    private static final float PAD         = 40f;

    private static final Color COL_BG       = new Color(0f,  0f,  0f,  0.82f);
    private static final Color COL_CURRENT  = new Color(1f,  0.95f, 0.2f, 1f);
    private static final Color COL_COMBAT   = new Color(0.75f, 0.75f, 0.75f, 1f);
    private static final Color COL_BOSS     = new Color(0.9f, 0.15f, 0.15f, 1f);
    private static final Color COL_SHOP     = new Color(0.2f, 0.85f, 0.25f, 1f);
    private static final Color COL_EVENT    = new Color(0.3f, 0.45f, 1f,    1f);
    private static final Color COL_SPAWN    = new Color(0.6f, 0.9f,  1f,    1f);
    private static final Color COL_DIM      = new Color(0.22f, 0.22f, 0.22f, 0.85f);
    private static final Color COL_LINE     = new Color(0.55f, 0.55f, 0.55f, 1f);
    private static final Color COL_LINE_DIM = new Color(0.22f, 0.22f, 0.22f, 0.7f);
    private static final Color COL_BORDER   = new Color(1f, 1f, 1f, 0.6f);
    private static final Color COL_LABEL_DARK = Color.BLACK;
    private static final Color COL_LABEL_LIGHT = Color.WHITE;

    public MapOverlay(GameRun gameRun) {
        this.gameRun = gameRun;
        this.shapes  = new ShapeRenderer();
        this.font    = new BitmapFont();
        font.getData().setScale(0.85f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        SectionLayout layout = gameRun.getActiveSectionLayout();
        if (layout == null) return;

        RoomNode[][] grid = layout.grid;
        int w = grid.length;
        int h = grid[0].length;

        float stageW = getStage().getWidth();
        float stageH = getStage().getHeight();
        float totalW = w * CELL_W;
        float totalH = h * CELL_H;
        float originX = (stageW - totalW) / 2f;
        float originY = (stageH - totalH) / 2f;

        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(getStage().getCamera().combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Dark background panel
        shapes.setColor(COL_BG);
        shapes.rect(originX - PAD, originY - PAD, totalW + PAD * 2, totalH + PAD * 2);

        // Connections (drawn under rooms)
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                RoomNode room = grid[x][y];
                if (room == null || !room.revealed) continue;

                float cx = originX + x * CELL_W + CELL_W / 2f;
                float cy = originY + y * CELL_H + CELL_H / 2f;

                if (room.doors.contains(Direction.EAST) && x + 1 < w && grid[x + 1][y] != null && grid[x + 1][y].revealed) {
                    float nextCX = originX + (x + 1) * CELL_W + CELL_W / 2f;
                    boolean bothVisited = room.visited || grid[x + 1][y].visited;
                    shapes.setColor(bothVisited ? COL_LINE : COL_LINE_DIM);
                    shapes.rect(cx, cy - LINE_W / 2f, nextCX - cx, LINE_W);
                }
                if (room.doors.contains(Direction.NORTH) && y + 1 < h && grid[x][y + 1] != null && grid[x][y + 1].revealed) {
                    float nextCY = originY + (y + 1) * CELL_H + CELL_H / 2f;
                    boolean bothVisited = room.visited || grid[x][y + 1].visited;
                    shapes.setColor(bothVisited ? COL_LINE : COL_LINE_DIM);
                    shapes.rect(cx - LINE_W / 2f, cy, LINE_W, nextCY - cy);
                }
            }
        }

        // Rooms
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                RoomNode room = grid[x][y];
                if (room == null || !room.revealed) continue;

                float rx = originX + x * CELL_W + CELL_W / 2f - ROOM_W / 2f;
                float ry = originY + y * CELL_H + CELL_H / 2f - ROOM_H / 2f;

                // Fill
                shapes.setColor(roomColor(room, layout));
                shapes.rect(rx, ry, ROOM_W, ROOM_H);

                // Border
                shapes.setColor(COL_BORDER);
                shapes.rect(rx,              ry,              ROOM_W, BORDER);
                shapes.rect(rx,              ry + ROOM_H - BORDER, ROOM_W, BORDER);
                shapes.rect(rx,              ry,              BORDER, ROOM_H);
                shapes.rect(rx + ROOM_W - BORDER, ry,         BORDER, ROOM_H);
            }
        }

        shapes.end();
        batch.begin();

        // Room labels — only for visited rooms
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                RoomNode room = grid[x][y];
                if (room == null || !room.visited) continue;

                float rx = originX + x * CELL_W + CELL_W / 2f - ROOM_W / 2f;
                float ry = originY + y * CELL_H + CELL_H / 2f - ROOM_H / 2f;

                boolean isCurrent = room == layout.currentRoom;
                font.setColor(isCurrent ? COL_LABEL_DARK : COL_LABEL_LIGHT);
                String label = roomLabel(room.type);
                font.draw(batch, label, rx + 5, ry + ROOM_H - 6);
            }
        }
    }

    private Color roomColor(RoomNode room, SectionLayout layout) {
        if (room == layout.currentRoom) return COL_CURRENT;
        if (!room.visited)              return COL_DIM;
        switch (room.type) {
            case "boss":  return COL_BOSS;
            case "shop":  return COL_SHOP;
            case "event": case "bookshelf": return COL_EVENT;
            case "spawn": return COL_SPAWN;
            default:      return COL_COMBAT;
        }
    }

    private String roomLabel(String type) {
        switch (type) {
            case "boss":  return "B";
            case "shop":  return "$";
            case "event": case "bookshelf": return "?";
            case "spawn": return "S";
            default:      return "×";
        }
    }

    public void dispose() {
        shapes.dispose();
        font.dispose();
    }
}
