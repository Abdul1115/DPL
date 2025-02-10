package com.futureprograms.Animales.Services;

import com.futureprograms.Animales.Models.Animal;
import com.futureprograms.Animales.Repositories.AnimalInterface;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnimalService implements AnimalInterface {
    private static final List<Animal> animales;
    static {
        animales = new ArrayList<>();
        animales.add(new Animal(1, "Perro", "15", false));
    }
    public static Integer Id = animales.size() + 1;

    public List<Animal> getList()
    {
        return animales;
    }

    public Animal animalDetails(int id)
    {
        /*int index = 0;
        for(Animal animal : animales) {
            if(id == animal.getId())
            {
                return animal;
            }
            index++;
        }
        return null;*/

        return animales.stream()
                .filter(animal -> animal.getId() == id)
                .findFirst()
                .orElse(null);

    }

    public void animalCreate(Animal animal)
    {
        animal.setId(Id);
        Animal newAnimal = new Animal(animal.getId(), animal.getName(), animal.getAge(), animal.getExtinct());
        animales.add(newAnimal);
        Id++;
    }

    public Animal animalUpdate(int id, Animal updatedAnimal) {
        for (int i = 0; i < animales.size(); i++) {
            Animal currentAnimal = animales.get(i);
            if (currentAnimal.getId() == id) { // Encuentra el animal por ID
                animales.set(i, updatedAnimal); // Actualiza el animal en la posición correcta
                return updatedAnimal; // Devuelve el animal actualizado
            }
        }
        throw new NoSuchElementException("No animal found with id: " + id);
    }

    public void animalDelete(int id)
    {
        /*int index = 0;
        for(Animal animal : animales) {
            if(id == animal.getId())
            {
                animales.remove(index);
                break;
            }
            index++;
        }*/

        animales.stream()
                .filter(animal -> animal.getId() == id)
                .findFirst().ifPresent(animales::remove);
    }
}