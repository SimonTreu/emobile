package de.dfki.gs.model.elements

import java.util.concurrent.Callable

/**
 * Created by glenn on 07.04.14.
 */
class EnergyConsumptionModel implements Callable<Double> {

    private int kmh;
    private int temperature;
    private int factor;

    private double km
    private double energyConsumption

    public EnergyConsumptionModel() {

    }

    /**
     * implementation of http://www2.isr.uc.pt/~carlospatrao/VE/A_sustainability_assessment_of_EVs.pdf
     *
     * only appriximated by (120 -> 23 kWh) as linear function of speed
     *
     * TODO: until we don't have realistic data from data boxes in cars, we only use energyConsumption
     *
     * @return
     * @throws Exception
     */
    @Override
    public Double call() throws Exception {

        // energyConsumption is in [ kWh / 100 km ]

        // return kmh * 23/120 ;
        return  ( km / 100 ) * energyConsumption
    }


    public EnergyConsumptionModel setParameters( double km, int kmh, int temperature, int factor, double energyConsumption ) {

        this.kmh = kmh;
        this.temperature = temperature;
        this.factor = factor;
        this.km = km

        this.energyConsumption = energyConsumption

        return this;
    }


    int getKmh() {
        return kmh
    }

    void setKmh(int kmh) {
        this.kmh = kmh
    }

    int getTemperature() {
        return temperature
    }

    void setTemperature(int temperature) {
        this.temperature = temperature
    }

    int getFactor() {
        return factor
    }

    void setFactor(int factor) {
        this.factor = factor
    }

    double getEnergyConsumption() {
        return energyConsumption
    }

    void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption
    }

    double getKm() {
        return km
    }

    void setKm(double km) {
        this.km = km
    }
}
