# 1. 仅保留 com.ruoyi.app 包下的所有 Activity 类（避免Android系统无法识别组件）
-keep public class com.ruoyi.app.** extends android.app.Activity { *; }

# 2. 仅保留 com.ruoyi.app 包下实现 Serializable 接口的类及序列化字段
-keep class com.ruoyi.app.** implements java.io.Serializable { *; }
# 保留序列化类的 serialVersionUID 字段（避免反序列化失败）
-keepclassmembers class com.ruoyi.app.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 3. 保留 com.ruoyi.app 包下所有类的公共方法（仅混淆私有方法/字段）
-keepclassmembers public class com.ruoyi.app.** {
    public <methods>;
}