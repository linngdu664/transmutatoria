package com.linngdu664.transmutatoria.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CoordinateConverter {
    private final Vec3 cameraPos;
    private final Matrix3f rotMat;
    private final float tanHalfFovy;
    private final float tanHalfFovx;

    public CoordinateConverter(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        GameRenderer gameRenderer = mc.gameRenderer;
        Camera camera = gameRenderer.getMainCamera();
        this.cameraPos = camera.position();
        this.rotMat = new Matrix3f().rotation(camera.rotation().conjugate(new Quaternionf()));      // make rot mat
        float fovy = camera.getFov() * Mth.DEG_TO_RAD;
        this.tanHalfFovy = Mth.sin(fovy * 0.5F) / Mth.cos(fovy * 0.5F);
        this.tanHalfFovx = this.tanHalfFovy * (float) window.getWidth() / (float) window.getHeight();
    }

    public void convertAndConsume(List<Pair<Vec3, Consumer<Vec2>>> points, int guiWidth, int guiHeight) {
        for (Pair<Vec3, Consumer<Vec2>> point : points) {
            convertAndConsume(point, guiWidth, guiHeight, 0, 0);
        }
    }

    public void convertAndConsume(List<Pair<Vec3, Consumer<Vec2>>> points, int guiWidth, int guiHeight, int widthProtect, int heightProtect) {
        for (Pair<Vec3, Consumer<Vec2>> point : points) {
            convertAndConsume(point, guiWidth, guiHeight, widthProtect, heightProtect);
        }
    }

    public void convertAndConsume(Pair<Vec3, Consumer<Vec2>> point, int guiWidth, int guiHeight) {
        convertAndConsume(point, guiWidth, guiHeight, 0, 0);
    }

    public void convertAndConsume(Pair<Vec3, Consumer<Vec2>> point, int guiWidth, int guiHeight, int widthProtect, int heightProtect) {
        point.getB().accept(convert(point.getA(), guiWidth, guiHeight, widthProtect, heightProtect));
    }

    public List<Vec2> convert(List<Vec3> points, int guiWidth, int guiHeight) {
        ArrayList<Vec2> arrayList = new ArrayList<>(points.size());
        for (Vec3 vec3 : points) {
            arrayList.add(convert(vec3, guiWidth, guiHeight, 0, 0));
        }
        return arrayList;
    }

    public List<Vec2> convert(List<Vec3> points, int guiWidth, int guiHeight, int widthProtect, int heightProtect) {
        ArrayList<Vec2> arrayList = new ArrayList<>(points.size());
        for (Vec3 vec3 : points) {
            arrayList.add(convert(vec3, guiWidth, guiHeight, widthProtect, heightProtect));
        }
        return arrayList;
    }

    public Vec2 convert(Vec3 vec3, int guiWidth, int guiHeight) {
        return convert(vec3, guiWidth, guiHeight, 0, 0);
    }

    public Vec2 convert(Vec3 vec3, int guiWidth, int guiHeight, int widthProtect, int heightProtect) {
        Vector3f vector3f = new Vector3f((float) (vec3.x - cameraPos.x), (float) (vec3.y - cameraPos.y), (float) (vec3.z - cameraPos.z));
        rotMat.transform(vector3f);
        float rx = vector3f.x / -vector3f.z / tanHalfFovx;
        float xScreen = vector3f.z >= 0 ? (vector3f.x >= 0 ? guiWidth - widthProtect : widthProtect) : Mth.clamp(guiWidth * 0.5F * (1 + rx), widthProtect, guiWidth - widthProtect);
        float ry = vector3f.y / -vector3f.z / tanHalfFovy;
        float yScreen = Mth.clamp(guiHeight * 0.5F * (1 - ry), heightProtect, guiHeight - heightProtect);
        return new Vec2(xScreen, yScreen);
    }
}
