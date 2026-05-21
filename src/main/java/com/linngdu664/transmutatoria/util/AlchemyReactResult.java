package com.linngdu664.transmutatoria.util;

import java.util.Objects;

public class AlchemyReactResult {
    private int essenceStateIncrease;
    private int polarityIncrease;
    private int entropyIncrease;
    private boolean isTriggerDamage;
    private boolean isClearItemStack;

    public AlchemyReactResult(int essenceStateIncrease, int polarityIncrease, int entropyIncrease, boolean isTriggerDamage, boolean isClearItemStack) {
        this.essenceStateIncrease = essenceStateIncrease;
        this.polarityIncrease = polarityIncrease;
        this.entropyIncrease = entropyIncrease;
        this.isTriggerDamage = isTriggerDamage;
        this.isClearItemStack = isClearItemStack;
    }

    public int getEssenceStateIncrease() {
        return essenceStateIncrease;
    }

    public void setEssenceStateIncrease(int essenceStateIncrease) {
        this.essenceStateIncrease = essenceStateIncrease;
    }

    public int getPolarityIncrease() {
        return polarityIncrease;
    }

    public void setPolarityIncrease(int polarityIncrease) {
        this.polarityIncrease = polarityIncrease;
    }

    public int getEntropyIncrease() {
        return entropyIncrease;
    }

    public void setEntropyIncrease(int entropyIncrease) {
        this.entropyIncrease = entropyIncrease;
    }

    public boolean isTriggerDamage() {
        return isTriggerDamage;
    }

    public void setTriggerDamage(boolean triggerDamage) {
        isTriggerDamage = triggerDamage;
    }

    public boolean isClearItemStack() {
        return isClearItemStack;
    }

    public void setClearItemStack(boolean clearItemStack) {
        isClearItemStack = clearItemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AlchemyReactResult result = (AlchemyReactResult) o;
        return essenceStateIncrease == result.essenceStateIncrease && polarityIncrease == result.polarityIncrease && entropyIncrease == result.entropyIncrease && isTriggerDamage == result.isTriggerDamage && isClearItemStack == result.isClearItemStack;
    }

    @Override
    public int hashCode() {
        return Objects.hash(essenceStateIncrease, polarityIncrease, entropyIncrease, isTriggerDamage, isClearItemStack);
    }

    @Override
    public String toString() {
        return "AlchemyReactResult{" +
                "essenceStateIncrease=" + essenceStateIncrease +
                ", polarityIncrease=" + polarityIncrease +
                ", entropyIncrease=" + entropyIncrease +
                ", isTriggerDamage=" + isTriggerDamage +
                ", isClearItemStack=" + isClearItemStack +
                '}';
    }
}
