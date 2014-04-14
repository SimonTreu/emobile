package de.dfki.gs.model.elements

import de.dfki.gs.domain.CarType

import java.util.concurrent.Callable

/**
 * Created by glenn on 01.04.14.
 */
class ModelCar {

    /**
     * car has a maximum limit of energy for its battery
     */
    private double maxEnergy

    /**
     * car consumes energy from battery, currentEnergy contains the current energy
     */
    private double currentEnergy


    private CarType carType

    /**
     * car has a name, to identify and group in results
     */
    private String carName

    /**
     * energy consumption model
     *
     * @return
     */
    private EnergyConsumptionModel func

    /**
     * absolute energy level under which car looks for a filling station
     */
    private Double absoluteSearchLimit

    /**
     * relative energy level in % under which car looks for a filling station
     */
    private Double relativeSearchLimit

    /**
     * energy consumption model
     *
     * @return
     */
    public double energyUsage( double km, int kmh, int temperature, int factor ) {

        // TODO: default value!
        double currentCosumption = 15;
        try {

            currentCosumption = func.setParameters( km, kmh, temperature, factor, carType.energyConsumption ).call()

        } catch ( Exception e ) {

        }

        return currentCosumption
    }


    private ModelCar() {}

    /**
     * this factory method creates a car, with a energy consumption model and a car name
     *
     * @param func
     * @param carName
     * @return
     */
    public static ModelCar createModelCar( EnergyConsumptionModel func, CarType carType, Double absoluteSearchLimit, Double relativeSearchLimit ) {

        ModelCar modelCar = new ModelCar();
        modelCar.func = func;
        modelCar.carType = carType
        modelCar.absoluteSearchLimit = absoluteSearchLimit;
        modelCar.relativeSearchLimit = relativeSearchLimit;

        modelCar.maxEnergy = carType.maxEnergyLoad
        modelCar.currentEnergy = carType.maxEnergyLoad
        modelCar.carName = carType.name

        return modelCar;
    }

    Double getRelativeSearchLimit() {
        return relativeSearchLimit
    }

    Double getAbsoluteSearchLimit() {
        return absoluteSearchLimit
    }

    String getCarName() {
        return carName
    }

    double getCurrentEnergy() {
        return currentEnergy
    }

    double getMaxEnergy() {
        return maxEnergy
    }

    void setCurrentEnergy(double currentEnergy) {
        this.currentEnergy = currentEnergy
    }

    CarType getCarType() {
        return carType
    }
}
