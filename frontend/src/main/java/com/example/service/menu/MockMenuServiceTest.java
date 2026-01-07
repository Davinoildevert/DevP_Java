package com.example.service.menu;

public class MockMenuServiceTest {

    public static void main(String[] args) {
        try {
            MenuService mock = MenuServiceFactory.getInstance();


            System.out.println("CATEGORIES JSON = " + mock.getCategories().size());
            System.out.println("PLATS (categorie 2) JSON = " + mock.getPlatsByCategorie(2).size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
