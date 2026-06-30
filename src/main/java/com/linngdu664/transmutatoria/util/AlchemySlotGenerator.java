package com.linngdu664.transmutatoria.util;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.*;

// 连通六边形槽位生成
public class AlchemySlotGenerator {
    public static void generate(ItemStack scrollStack, int count, RandomSource random) {
        List<V2I> positions = generateConnectedHexPositions(count, random);
        List<AbstractAlchemySlot> slots = new ArrayList<>(positions.size());
        EssenceMetal[] allMetals = EssenceMetal.values();

        for (V2I pos : positions) {
            SlotType slotType = pickSlotType(positions.size(), random);
            // todo 概率要在配置文件里调吗？
            EssenceMetal metal;
            if (random.nextFloat() <= 0.02f) {
                metal = EssenceMetal.L;
            } else {
                metal = allMetals[random.nextInt(allMetals.length - 1)];
            }
            slots.add(AbstractAlchemySlot.create(slotType, metal, pos.x(), pos.y(), true));
        }

        scrollStack.set(InitDataComponents.ALCHEMY_SLOTS, slots);
    }

    private static final int[][] HEX_OFFSETS = {
            {0, -2}, {1, -1}, {1, 1}, {0, 2}, {-1, 1}, {-1, -1}
    };

    private static final SlotType[] SPECIAL_TYPES = Arrays.stream(SlotType.values())
            .filter(t -> t != SlotType.NORMAL)
            .toArray(SlotType[]::new);

    private static final int MAX_GRID_EXTENT = 12;

    /**
     * 用 frontier 扩张算法生成 count 个连通的六边形坐标，从 (0,0) 开始。
     * 约束生成区域的总宽度和总高度均不超过 {@value MAX_GRID_EXTENT}。
     */
    private static List<V2I> generateConnectedHexPositions(int count, RandomSource random) {
        Set<V2I> placed = new HashSet<>();
        List<V2I> frontier = new ArrayList<>();

        V2I origin = new V2I(0, 0);
        placed.add(origin);
        int minX = origin.x(), maxX = origin.x();
        int minY = origin.y(), maxY = origin.y();
        addFrontier(origin, placed, frontier);

        while (placed.size() < count && !frontier.isEmpty()) {
            int idx = random.nextInt(frontier.size());
            V2I pos = frontier.remove(idx);
            if (placed.contains(pos)) {
                continue;
            }
            int newMinX = Math.min(minX, pos.x());
            int newMaxX = Math.max(maxX, pos.x());
            int newMinY = Math.min(minY, pos.y());
            int newMaxY = Math.max(maxY, pos.y());
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

    private static void addFrontier(V2I pos, Set<V2I> placed, List<V2I> frontier) {
        for (int[] off : HEX_OFFSETS) {
            V2I neighbor = new V2I(pos.x() + off[0], pos.y() + off[1]);
            if (!placed.contains(neighbor) && !frontier.contains(neighbor)) {
                frontier.add(neighbor);
            }
        }
    }

    // todo 后续可能在配置文件中加入特殊槽位概率？
    private static SlotType pickSlotType(int slotCount, RandomSource random) {
        if (slotCount <= 8) return SlotType.NORMAL;
        // 24 级时 25% 概率为特殊槽位
        // 8 级时 0% 概率为特殊槽位
        if (random.nextFloat() < 48f / (slotCount + 40)) return SlotType.NORMAL;
        return SPECIAL_TYPES[random.nextInt(SPECIAL_TYPES.length)];
    }

    private static boolean shouldSetShowType(SlotType slotType, int slotCount, RandomSource random) {
        if (slotCount <= 8 || slotType == SlotType.NORMAL) return true;
        return random.nextFloat() < 16f / (slotCount + 8);
    }
}
