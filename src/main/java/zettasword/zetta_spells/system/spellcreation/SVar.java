package zettasword.zetta_spells.system.spellcreation;

public class SVar {

    int type = 0;
    private int valueInt = 0;
    private boolean valueBoolean = false;
    private String valueString = "";

    public static SVar init(int valueInt){
        return new SVar(valueInt);
    }

    public static SVar init(Boolean valueBoolean){
        return new SVar(valueBoolean);
    }

    public static SVar init(String valueString){
        return new SVar(valueString);
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

    public int getType() {
        return this.type;
    }

    public int getInt() {
        return valueInt;
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
}
