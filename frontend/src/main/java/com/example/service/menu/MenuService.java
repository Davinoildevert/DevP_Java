package com.example.service.menu;

import com.example.model.menu.Categorie;
import com.example.model.menu.Plat;

import java.util.List;

public interface MenuService {
    List<Categorie> getCategories() throws Exception;
    List<Plat> getAllPlats() throws Exception;
    List<Plat> getPlatsByCategorie(int categorieId) throws Exception;
}
