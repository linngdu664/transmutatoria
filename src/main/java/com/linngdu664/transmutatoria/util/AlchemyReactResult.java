package com.linngdu664.transmutatoria.util;

import java.util.Objects;

public class AlchemyReactResult {
    private int entropyIncrease;
    private int polarityIncrease;
    private boolean isTriggerDamage;

    public AlchemyReactResult(int entropyIncrease, int polarityIncrease, boolean isTriggerDamage) {
        this.entropyIncrease = entropyIncrease;
        this.polarityIncrease = polarityIncrease;
        this.isTriggerDamage = isTriggerDamage;
    }

    public int getEntropyIncrease() {
        return entropyIncrease;
    }

    public int getPolarityIncrease() {
        return polarityIncrease;
    }

    public boolean isTriggerDamage() {
        return isTriggerDamage;
    }

    public void setEntropyIncrease(int entropyIncrease) {
        this.entropyIncrease = entropyIncrease;
    }

    public void setPolarityIncrease(int polarityIncrease) {
        this.polarityIncrease = polarityIncrease;
    }

    public void setTriggerDamage(boolean triggerDamage) {
        this.isTriggerDamage = triggerDamage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AlchemyReactResult that = (AlchemyReactResult) o;
        return entropyIncrease == that.entropyIncrease && polarityIncrease == that.polarityIncrease && isTriggerDamage == that.isTriggerDamage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entropyIncrease, polarityIncrease, isTriggerDamage);
    }

    @Override
    public String toString() {
        return "AlchemyReactResult{" +
                "entropyIncrease=" + entropyIncrease +
                ", polarityIncrease=" + polarityIncrease +
                ", isTriggerDamage=" + isTriggerDamage +
                '}';
    }
}
