package org.cyclops.integrateddynamics.core.evaluate.variable;

import com.google.common.collect.Maps;
import org.cyclops.integrateddynamics.core.evaluate.InvalidValueTypeException;

import java.util.Map;

/**
 * @author rubensworks
 */
public class ValueTypeLightLevelRegistry implements IValueTypeLightLevelRegistry {

    private static ValueTypeLightLevelRegistry INSTANCE = new ValueTypeLightLevelRegistry();

    private final Map<IValueType<?>, ILightLevelCalculator> lightLevelCalculatorMap = Maps.newHashMap();

    private ValueTypeLightLevelRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static ValueTypeLightLevelRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <L extends ILightLevelCalculator<V>, V extends IValue> L register(IValueType<V> valueType, L lightLevelCalculator) {
        lightLevelCalculatorMap.put(valueType, lightLevelCalculator);
        return lightLevelCalculator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends IValue> ILightLevelCalculator<V> getLightLevelCalculator(IValueType<V> valueType) {
        return lightLevelCalculatorMap.get(valueType);
    }

    @Override
    public <V extends IValue> int getLightLevel(V value) throws InvalidValueTypeException {
        IValueType<V> valueType = value.getType();
        ILightLevelCalculator lightLevelCalculator = getLightLevelCalculator(valueType);
        if(lightLevelCalculator != null) {
            return lightLevelCalculator.getLightLevel(value);
        }
        for (Map.Entry<IValueType<?>, ILightLevelCalculator> entry : lightLevelCalculatorMap.entrySet()) {
            if(value.canCast(entry.getKey())) {
                return entry.getValue().getLightLevel(value.cast(entry.getKey()));
            }
        }
        throw new InvalidValueTypeException(String.format("The value %s can not be used to derive a light level.", value));
    }

}