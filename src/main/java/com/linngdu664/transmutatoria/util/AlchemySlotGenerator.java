package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class AlchemySlotGenerator {
    public static void generate(ItemStack scrollStack, int count, RandomSource random) {
        List<HexPos> positions = generateConnectedHexPositions(count, random);
        List<AbstractAlchemySlot> slots = new ArrayList<>(positions.size());
        EssenceMetal[] allMetals = EssenceMetal.values();

        for (HexPos pos : positions) {
            slots.add(AbstractAlchemySlot.create(
                    pickSlotType(positions.size(), random),
                    allMetals[random.nextInt(allMetals.length)],
                    pos.x, pos.y, true
            ));
        }

        scrollStack.set(InitDataComponents.ALCHEMY_SLOTS, slots);
    }


    // region 连通六边形槽位生成

    private static final int[][] HEX_OFFSETS = {
            {0, -2}, {1, -1}, {1, 1}, {0, 2}, {-1, 1}, {-1, -1}
    };

    private static final SlotType[] SPECIAL_TYPES = Arrays.stream(SlotType.values())
            .filter(t -> t != SlotType.NORMAL)
            .toArray(SlotType[]::new);

    private record HexPos(int x, int y) {}

    private static final int MAX_GRID_EXTENT = 12;

    /**
     * 用 frontier 扩张算法生成 count 个连通的六边形坐标，从 (0,0) 开始。
     * 约束生成区域的总宽度和总高度均不超过 {@value MAX_GRID_EXTENT}。
     */
    private static List<HexPos> generateConnectedHexPositions(int count, RandomSource random) {
        Set<HexPos> placed = new HashSet<>();
        List<HexPos> frontier = new ArrayList<>();

        HexPos origin = new HexPos(0, 0);
        placed.add(origin);
        int minX = origin.x, maxX = origin.x;
        int minY = origin.y, maxY = origin.y;
        addFrontier(origin, placed, frontier);

        while (placed.size() < count && !frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            HexPos pos = frontier.remove(idx);
            if (placed.contains(pos)) {
                continue;
            }
            int newMinX = Math.min(minX, pos.x);
            int newMaxX = Math.max(maxX, pos.x);
            int newMinY = Math.min(minY, pos.y);
            int newMaxY = Math.max(maxY, pos.y);
            if (newMaxX - newMinX >= MAX_GRID_EXTENT || newMaxY - newMinY >= MAX_GRID_EXTENT) {
                continue;
            }
            placed.add(pos);
            minX = newMinX;
            maxX = newMaxX;
            minY = newMinY;
            maxY = newMaxY;
            addFrontier(pos, placed, frontier);
        }

        return new ArrayList<>(placed);
    }

    private static void addFrontier(HexPos pos, Set<HexPos> placed, List<HexPos> frontier) {
        for (int[] off : HEX_OFFSETS) {
            HexPos neighbor = new HexPos(pos.x + off[0], pos.y + off[1]);
            if (!placed.contains(neighbor) && !frontier.contains(neighbor)) {
                frontier.add(neighbor);
            }
        }
    }

    // todo 后续可能在配置文件中加入特殊槽位概率？
    private static SlotType pickSlotType(int slotCount, RandomSource random) {
        if (slotCount <= 8) return SlotType.NORMAL;
        if (random.nextFloat() < 0.75f) return SlotType.NORMAL;
        return SPECIAL_TYPES[random.nextInt(SPECIAL_TYPES.length)];
    }
}
