package com.futureprograms.Animales.Repositories;

import com.futureprograms.Animales.Models.Animal;
import java.util.List;

public interface AnimalInterface{
    List<Animal> getList();
    Animal animalDetails(int id);
    void animalCreate(Animal animal);
    Animal animalUpdate(int id, Animal animal);
    void animalDelete(int id);
}