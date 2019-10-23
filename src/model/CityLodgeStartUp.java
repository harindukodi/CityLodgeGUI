package model;

import model.entities.CityLodge;

/**
 * The Startup class responsible for the CityLodge Hotel.
 *
 * @author Harindu Kodituwakku
 */


public class CityLodgeStartUp {

    public static void main(String[] args) {
        try {
            new CityLodge().Start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("All rights reserved. CityLodge 2019.");

        }
    }
}
