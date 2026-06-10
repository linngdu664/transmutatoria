package com.linngdu664.transmutatoria.client.tool;

import java.util.HashMap;

public abstract class Easing {
    public static final HashMap<String, Easing> EASINGS = new HashMap<>();
    public static final Easing LINEAR = new Easing("linear") {
        // 线性移动：从起点到终点全程保持同样速度，没有加速或减速。
        public float ease(float t, float b, float c, float d) {
            return c * t / d + b;
        }
    };
    public static final Easing QUAD_IN = new Easing("quadIn") {
        // 二次方缓入：一开始很慢，然后速度按平方增长，越到后面越快。
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t + b;
        }
    };
    public static final Easing QUAD_OUT = new Easing("quadOut") {
        // 二次方缓出：一开始较快，越接近终点越慢，最后平滑停下。
        public float ease(float t, float b, float c, float d) {
            return -c * (t /= d) * (t - 2.0F) + b;
        }
    };
    public static final Easing QUAD_IN_OUT = new Easing("quadInOut") {
        // 二次方缓入缓出：前半段加速，后半段减速，中点附近速度最快。
        public float ease(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t + b : -c / 2.0F * (--t * (t - 2.0F) - 1.0F) + b;
        }
    };
    public static final Easing CUBIC_IN = new Easing("cubicIn") {
        // 三次方缓入：起步比二次方更慢，随后加速更明显，后段冲得更快。
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t * t + b;
        }
    };
    public static final Easing CUBIC_OUT = new Easing("cubicOut") {
        // 三次方缓出：开头速度很快，后段明显减速，靠近终点时柔和贴近。
        public float ease(float t, float b, float c, float d) {
            return c * ((t = t / d - 1.0F) * t * t + 1.0F) + b;
        }
    };
    public static final Easing CUBIC_IN_OUT = new Easing("cubicInOut") {
        // 三次方缓入缓出：两端更慢，中间更快，整体比二次方更有冲劲。
        public float ease(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t + b : c / 2.0F * ((t -= 2.0F) * t * t + 2.0F) + b;
        }
    };
    public static final Easing QUARTIC_IN = new Easing("quarticIn") {
        // 四次方缓入：起步非常慢，后段急剧加速，适合“蓄力后冲出”的感觉。
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t * t * t + b;
        }
    };
    public static final Easing QUARTIC_OUT = new Easing("quarticOut") {
        // 四次方缓出：一开始很猛，随后快速放缓，终点前几乎贴着目标滑入。
        public float ease(float t, float b, float c, float d) {
            return -c * ((t = t / d - 1.0F) * t * t * t - 1.0F) + b;
        }
    };
    public static final Easing QUARTIC_IN_OUT = new Easing("quarticInOut") {
        // 四次方缓入缓出：开头和结尾都很慢，中段加速很强，运动节奏更夸张。
        public float ease(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t * t + b : -c / 2.0F * ((t -= 2.0F) * t * t * t - 2.0F) + b;
        }
    };
    public static final Easing QUINTIC_IN = new Easing("quinticIn") {
        // 五次方缓入：起步最慢的一类，末段突然变快，像强烈蓄能后的启动。
        public float ease(float t, float b, float c, float d) {
            return c * (t /= d) * t * t * t * t + b;
        }
    };
    public static final Easing QUINTIC_OUT = new Easing("quinticOut") {
        // 五次方缓出：前段移动极快，后段长时间慢慢贴近终点，停靠感最强。
        public float ease(float t, float b, float c, float d) {
            return c * ((t = t / d - 1.0F) * t * t * t * t + 1.0F) + b;
        }
    };
    public static final Easing QUINTIC_IN_OUT = new Easing("quinticInOut") {
        // 五次方缓入缓出：两端极慢，中间爆发式通过，适合强烈但仍平滑的过渡。
        public float ease(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * t * t * t + b : c / 2.0F * ((t -= 2.0F) * t * t * t * t + 2.0F) + b;
        }
    };
    public static final Easing SINE_IN = new Easing("sineIn") {
        // 正弦缓入：按四分之一正弦曲线启动，起步柔和，随后自然加速。
        public float ease(float t, float b, float c, float d) {
            return -c * (float) Math.cos((double) (t / d) * 1.5707963267948966) + c + b;
        }
    };
    public static final Easing SINE_OUT = new Easing("sineOut") {
        // 正弦缓出：按四分之一正弦曲线收尾，前快后慢，终点停得自然。
        public float ease(float t, float b, float c, float d) {
            return c * (float) Math.sin((double) (t / d) * 1.5707963267948966) + b;
        }
    };
    public static final Easing SINE_IN_OUT = new Easing("sineInOut") {
        // 正弦缓入缓出：半个余弦波形，起止都很柔，中间速度最高。
        public float ease(float t, float b, float c, float d) {
            return -c / 2.0F * ((float) Math.cos(Math.PI * (double) t / (double) d) - 1.0F) + b;
        }
    };
    public static final Easing EXPO_IN = new Easing("expoIn") {
        // 指数缓入：前期几乎不动，后期速度指数级暴涨，像突然被拉出去。
        public float ease(float t, float b, float c, float d) {
            return t == 0.0F ? b : c * (float) Math.pow(2.0, 10.0F * (t / d - 1.0F)) + b;
        }
    };
    public static final Easing EXPO_OUT = new Easing("expoOut") {
        // 指数缓出：一开始几乎立刻接近终点，然后用很长尾巴慢慢贴合。
        public float ease(float t, float b, float c, float d) {
            return t == d ? b + c : c * (-((float) Math.pow(2.0, -10.0F * t / d)) + 1.0F) + b;
        }
    };
    public static final Easing EXPO_IN_OUT = new Easing("expoInOut") {
        // 指数缓入缓出：前半几乎静止后突然加速，后半快速接近再慢慢停住。
        public float ease(float t, float b, float c, float d) {
            if (t == 0.0F) {
                return b;
            } else if (t == d) {
                return b + c;
            } else {
                return (t /= d / 2.0F) < 1.0F ? c / 2.0F * (float) Math.pow(2.0, 10.0F * (t - 1.0F)) + b : c / 2.0F * (-((float) Math.pow(2.0, -10.0F * --t)) + 2.0F) + b;
            }
        }
    };
    public static final Easing CIRC_IN = new Easing("circIn") {
        // 圆形缓入：沿圆弧感觉启动，开头压得很慢，越往后越快。
        public float ease(float t, float b, float c, float d) {
            return -c * ((float) Math.sqrt(1.0F - (t /= d) * t) - 1.0F) + b;
        }
    };
    public static final Easing CIRC_OUT = new Easing("circOut") {
        // 圆形缓出：沿圆弧感觉靠近终点，开始快，结束时明显减速。
        public float ease(float t, float b, float c, float d) {
            return c * (float) Math.sqrt(1.0F - (t = t / d - 1.0F) * t) + b;
        }
    };
    public static final Easing CIRC_IN_OUT = new Easing("circInOut") {
        // 圆形缓入缓出：像沿圆弧的两段运动，前半加速，后半减速。
        public float ease(float t, float b, float c, float d) {
            return (t /= d / 2.0F) < 1.0F ? -c / 2.0F * ((float) Math.sqrt(1.0F - t * t) - 1.0F) + b : c / 2.0F * ((float) Math.sqrt(1.0F - (t -= 2.0F) * t) + 1.0F) + b;
        }
    };
    public static final Easing.Elastic ELASTIC_IN = new Easing.ElasticIn();
    public static final Easing.Elastic ELASTIC_OUT = new Easing.ElasticOut();
    public static final Easing.Elastic ELASTIC_IN_OUT = new Easing.ElasticInOut();
    public static final Easing.Back BACK_IN = new Easing.BackIn();
    public static final Easing.Back BACK_OUT = new Easing.BackOut();
    public static final Easing.Back BACK_IN_OUT = new Easing.BackInOut();
    public static final Easing BOUNCE_OUT = new Easing("bounceOut") {
        // 弹跳缓出：先冲向终点，再按多段越来越小的弹跳贴近目标。
        public float ease(float t, float b, float c, float d) {
            if ((t /= d) < 0.36363637F) {
                return c * 7.5625F * t * t + b;
            } else if (t < 0.72727275F) {
                return c * (7.5625F * (t -= 0.54545456F) * t + 0.75F) + b;
            } else {
                return t < 0.90909094F ? c * (7.5625F * (t -= 0.8181818F) * t + 0.9375F) + b : c * (7.5625F * (t -= 0.95454544F) * t + 0.984375F) + b;
            }
        }
    };
    public static final Easing BOUNCE_IN = new Easing("bounceIn") {
        // 弹跳缓入：把弹跳缓出反过来，开始先小幅弹动，最后冲向终点。
        public float ease(float t, float b, float c, float d) {
            return c - Easing.BOUNCE_OUT.ease(d - t, 0.0F, c, d) + b;
        }
    };
    public static final Easing BOUNCE_IN_OUT = new Easing("bounceInOut") {
        // 弹跳缓入缓出：前半段反向弹跳进入，中点后正向弹跳落到终点。
        public float ease(float t, float b, float c, float d) {
            return t < d / 2.0F ? Easing.BOUNCE_IN.ease(t * 2.0F, 0.0F, c, d) * 0.5F + b : Easing.BOUNCE_OUT.ease(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;
        }
    };
    public final String name;

    public Easing(String name) {
        this.name = name;
        EASINGS.put(name, this);
    }

    public static Easing valueOf(String name) {
        return EASINGS.get(name);
    }

    // 通用参数：t 是当前经过时间，b 是起始值，c 是总位移量，d 是总时长。
    public abstract float ease(float var1, float var2, float var3, float var4);

    public static class ElasticIn extends Easing.Elastic {
        public ElasticIn(float amplitude, float period) {
            super("elasticIn", amplitude, period);
        }

        public ElasticIn() {
            super("elasticIn");
        }

        // 弹性缓入：先向反方向拉伸并振荡，随后带着弹簧感加速冲向终点。
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0F) {
                return b;
            } else if ((t /= d) == 1.0F) {
                return b + c;
            } else {
                if (p == 0.0F) {
                    p = d * 0.3F;
                }

                float s;
                if (a < Math.abs(c)) {
                    a = c;
                    s = p / 4.0F;
                } else {
                    s = p / 6.2831855F * (float) Math.asin(c / a);
                }

                return -(a * (float) Math.pow(2.0, 10.0F * --t) * (float) Math.sin((double) (t * d - s) * 6.283185307179586 / (double) p)) + b;
            }
        }
    }

    public abstract static class Elastic extends Easing {
        private float amplitude;
        private float period;

        public Elastic(String name, float amplitude, float period) {
            super(name);
            this.amplitude = amplitude;
            this.period = period;
        }

        public Elastic(String name) {
            this(name, -1.0F, 0.0F);
        }

        public float getPeriod() {
            return this.period;
        }

        public void setPeriod(float period) {
            this.period = period;
        }

        public float getAmplitude() {
            return this.amplitude;
        }

        public void setAmplitude(float amplitude) {
            this.amplitude = amplitude;
        }
    }

    public static class ElasticOut extends Easing.Elastic {
        public ElasticOut(float amplitude, float period) {
            super("elasticOut", amplitude, period);
        }

        public ElasticOut() {
            super("elasticOut");
        }

        // 弹性缓出：先越过终点，再围绕终点来回振荡，振幅逐渐衰减到静止。
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0F) {
                return b;
            } else if ((t /= d) == 1.0F) {
                return b + c;
            } else {
                if (p == 0.0F) {
                    p = d * 0.3F;
                }

                float s;
                if (a < Math.abs(c)) {
                    a = c;
                    s = p / 4.0F;
                } else {
                    s = p / 6.2831855F * (float) Math.asin(c / a);
                }

                return a * (float) Math.pow(2.0, -10.0F * t) * (float) Math.sin((double) (t * d - s) * 6.283185307179586 / (double) p) + c + b;
            }
        }
    }

    public static class ElasticInOut extends Easing.Elastic {
        public ElasticInOut(float amplitude, float period) {
            super("elasticInOut", amplitude, period);
        }

        public ElasticInOut() {
            super("elasticInOut");
        }

        // 弹性缓入缓出：前半段先反向拉伸再加速，中点后越过终点并衰减振荡。
        public float ease(float t, float b, float c, float d) {
            float a = this.getAmplitude();
            float p = this.getPeriod();
            if (t == 0.0F) {
                return b;
            } else if ((t /= d / 2.0F) == 2.0F) {
                return b + c;
            } else {
                if (p == 0.0F) {
                    p = d * 0.45000002F;
                }

                float s;
                if (a < Math.abs(c)) {
                    a = c;
                    s = p / 4.0F;
                } else {
                    s = p / 6.2831855F * (float) Math.asin(c / a);
                }

                return t < 1.0F ? -0.5F * a * (float) Math.pow(2.0, 10.0F * --t) * (float) Math.sin((double) (t * d - s) * 6.283185307179586 / (double) p) + b : a * (float) Math.pow(2.0, -10.0F * --t) * (float) Math.sin((double) (t * d - s) * 6.283185307179586 / (double) p) * 0.5F + c + b;
            }
        }
    }

    public static class BackIn extends Easing.Back {
        public BackIn() {
            super("backIn");
        }

        public BackIn(float overshoot) {
            super("backIn", overshoot);
        }

        // 回退缓入：开始先向起点反方向退一点，再加速越过这段回拉后前进。
        public float ease(float t, float b, float c, float d) {
            float s = this.getOvershoot();
            return c * (t /= d) * t * ((s + 1.0F) * t - s) + b;
        }
    }

    public abstract static class Back extends Easing {
        public static final float DEFAULT_OVERSHOOT = 1.70158F;
        private float overshoot;

        public Back(String name) {
            this(name, 1.70158F);
        }

        public Back(String name, float overshoot) {
            super(name);
            this.overshoot = overshoot;
        }

        public float getOvershoot() {
            return this.overshoot;
        }

        public void setOvershoot(float overshoot) {
            this.overshoot = overshoot;
        }
    }

    public static class BackOut extends Easing.Back {
        public BackOut() {
            super("backOut");
        }

        public BackOut(float overshoot) {
            super("backOut", overshoot);
        }

        // 回退缓出：先越过终点一点，再往回收，最后停在准确的终点。
        public float ease(float t, float b, float c, float d) {
            float s = this.getOvershoot();
            return c * ((t = t / d - 1.0F) * t * ((s + 1.0F) * t + s) + 1.0F) + b;
        }
    }

    public static class BackInOut extends Easing.Back {
        public BackInOut() {
            super("backInOut");
        }

        public BackInOut(float overshoot) {
            super("backInOut", overshoot);
        }

        // 回退缓入缓出：开头先反向回拉，中间加速通过，结尾越过终点后回收。
        public float ease(float t, float b, float c, float d) {
            float s = this.getOvershoot();
            return (t /= d / 2.0F) < 1.0F ? c / 2.0F * t * t * (((s = (float) ((double) s * 1.525)) + 1.0F) * t - s) + b : c / 2.0F * ((t -= 2.0F) * t * (((s = (float) ((double) s * 1.525)) + 1.0F) * t + s) + 2.0F) + b;
        }
    }
}
