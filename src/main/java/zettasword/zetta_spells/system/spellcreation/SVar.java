package zettasword.zetta_spells.system.spellcreation;

import net.minecraft.world.phys.Vec3;

public class SVar {

    int type = 0;
    private int valueInt = 0;
    private boolean valueBoolean = false;
    private String valueString = "";
    private Vec3 valueVec = new Vec3(0,0,0);

    public static SVar init(int valueInt){
        return new SVar(valueInt);
    }

    public static SVar init(Boolean valueBoolean){
        return new SVar(valueBoolean);
    }

    public static SVar init(String valueString){
        return new SVar(valueString);
    }

    public static SVar init(Vec3 vec3){
        return new SVar(vec3);
    }

    public SVar(int valueInt){
        // Since type is already 0 by default.
        this.setInt(valueInt);
    }

    public SVar(boolean valueBoolean){
        this.type = SpellTypes.BOOLEAN.ordinal();
        this.setBoolean(valueBoolean);
    }

    public SVar(String valueString){
        this.type= SpellTypes.STRING.ordinal();
        this.setString(valueString);
    }

    public SVar(Vec3 vec3){
        this.type = SpellTypes.VECTOR.ordinal();
        this.setVec(vec3);
    }

    public int getType() {
        return this.type;
    }

    public int getInt() {
        return valueInt;
    }

    /** To not allow negative values. **/
    public int getIntSafe() {
        return Math.max(valueInt, 0);
    }


    /** To not allow negative values.
     * @param min Math.max returns the higher number, usually it is valueInt, but if caster defines it as -1 when it will return this.
     * @return Returns the higher of two values.
     */
    public int getIntSafe(int min) {
        return Math.max(valueInt, min);
    }

    public void setInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public boolean getBoolean() {
        return valueBoolean;
    }

    public void setBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public String getString() {
        return valueString;
    }

    public void setString(String valueString) {
        this.valueString = valueString;
    }

    public Vec3 getVec() {
        return valueVec;
    }

    public void setVec(Vec3 valueVec) {
        this.valueVec = valueVec;
    }
}
