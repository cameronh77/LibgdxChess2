package io.github.chess_sequel.engine.hub;

import io.github.chess_sequel.engine.section.Direction;

import java.util.LinkedHashMap;
import java.util.Map;

public class HubLayout {

    public final Map<String, HubRoom> rooms;
    public final HubRoom spawnRoom;
    public HubRoom currentRoom;

    private HubLayout(Map<String, HubRoom> rooms, HubRoom spawnRoom) {
        this.rooms = rooms;
        this.spawnRoom = spawnRoom;
        this.currentRoom = spawnRoom;
    }

    public HubRoom getRoomInDirection(Direction dir) {
        String id = currentRoom.connections.get(dir);
        return id != null ? rooms.get(id) : null;
    }

    /**
     * Builds the fixed hub layout:
     *
     *   [Throne 8×6] ─SOUTH/NORTH─ [Main 10×7] ─EAST─  [Portal-Strategy 5×5]
     *                                            ─WEST─  [Portal-Conflict 5×5]
     *                                            ─SOUTH─ [Portal-Loss     5×5]
     *
     * Spawn tile is the throne position (col=4, row=4) in the throne room.
     */
    public static HubLayout create() {
        // Throne room — spawn one tile in front of the throne (king faces it from the south)
        HubRoom throne = new HubRoom("throne", 8, 6, 4, 3, null);
        throne.doors.add(Direction.SOUTH);
        throne.connections.put(Direction.SOUTH, "main");

        // Main room — central hub, connects to all other rooms
        HubRoom main = new HubRoom("main", 10, 7, -1, -1, null);
        main.doors.add(Direction.NORTH);
        main.doors.add(Direction.EAST);
        main.doors.add(Direction.WEST);
        main.doors.add(Direction.SOUTH);
        main.connections.put(Direction.NORTH, "throne");
        main.connections.put(Direction.EAST,  "portal-strategy");
        main.connections.put(Direction.WEST,  "portal-future");
        main.connections.put(Direction.SOUTH, "portal-loss");

        // Strategy portal room (right offshoot)
        HubRoom portalStrategy = new HubRoom("portal-strategy", 5, 5, -1, -1, "strategy");
        portalStrategy.doors.add(Direction.WEST);
        portalStrategy.connections.put(Direction.WEST, "main");

        // Conflict portal room (left offshoot)
        HubRoom portalFuture = new HubRoom("portal-future", 5, 5, -1, -1, "conflict");
        portalFuture.doors.add(Direction.EAST);
        portalFuture.connections.put(Direction.EAST, "main");

        // Loss portal room (south offshoot)
        HubRoom portalLoss = new HubRoom("portal-loss", 5, 5, -1, -1, "loss");
        portalLoss.doors.add(Direction.NORTH);
        portalLoss.connections.put(Direction.NORTH, "main");

        Map<String, HubRoom> rooms = new LinkedHashMap<>();
        rooms.put(throne.id, throne);
        rooms.put(main.id, main);
        rooms.put(portalStrategy.id, portalStrategy);
        rooms.put(portalFuture.id, portalFuture);
        rooms.put(portalLoss.id, portalLoss);

        return new HubLayout(rooms, throne);
    }
}
