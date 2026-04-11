package zettasword.zetta_spells.system;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateUtils {

    /**
     * @param level        The world level
     * @param pos          The block position
     * @param propertyName The name of the property (e.g., "open", "powered", "active")
     */
    public static boolean toggleBooleanProperty(Level level, BlockPos pos, String propertyName) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Get the property from the block's state definition
        Property<?> property = block.getStateDefinition().getProperty(propertyName);

        // Verify the property exists and is a BooleanProperty
        if (!(property instanceof BooleanProperty booleanProperty)) {
            return false;
        }

        // Verify the current state has this property
        if (!state.hasProperty(booleanProperty)) {
            return false;
        }

        // Get current value and toggle it
        boolean currentValue = state.getValue(booleanProperty);
        BlockState newState = state.setValue(booleanProperty, !currentValue);

        // Apply the new state to the world
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        return true;
    }

    /**
     *
     * @param level      The world level
     * @param pos        The block position
     * @param propertyName The name of the property (e.g., "open", "powered", "active")
     * @param value_to_set The value to set property
     * @return true if the property was successfully toggled, false otherwise
     */
    public static boolean setIntProperty(Level level, BlockPos pos, String propertyName, int value_to_set) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Get the property from the block's state definition
        Property<?> property = block.getStateDefinition().getProperty(propertyName);

        if (!(property instanceof IntegerProperty integerProperty)) {
            return false;
        }

        // Verify the current state has this property
        if (!state.hasProperty(integerProperty)) {
            return false;
        }

        // Get current value and change it
        BlockState newState = state.setValue(integerProperty, value_to_set);

        // Apply the new state to the world
        level.setBlock(pos, newState, Block.UPDATE_ALL);

        return true;
    }

    /**
     * Sets a boolean BlockState property by name to a specific value.
     *
     * @param level      The world level
     * @param pos        The block position
     * @param propertyName The name of the boolean property
     * @param value      The value to set
     * @return true if successful, false otherwise
     */
    public static boolean setBooleanProperty(Level level, BlockPos pos, String propertyName, boolean value) {
        if (level.isClientSide) return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        Property<?> property = block.getStateDefinition().getProperty(propertyName);

        if (!(property instanceof BooleanProperty booleanProperty)) {
            return false;
        }

        if (!state.hasProperty(booleanProperty)) {
            return false;
        }

        BlockState newState = state.setValue(booleanProperty, value);
        level.setBlock(pos, newState, Block.UPDATE_ALL);

        return true;
    }

    /**
     * Checks if a block has a specific boolean property.
     */
    public static boolean hasBooleanProperty(BlockState state, String propertyName) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);
        return property instanceof BooleanProperty && state.hasProperty((BooleanProperty) property);
    }

    /**
     * Gets the current value of a boolean property.
     * Returns null if the property doesn't exist or isn't boolean.
     */
    public static Boolean getBooleanPropertyValue(BlockState state, String propertyName) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);
        if (property instanceof BooleanProperty booleanProperty && state.hasProperty(booleanProperty)) {
            return state.getValue(booleanProperty);
        }
        return null;
    }
}