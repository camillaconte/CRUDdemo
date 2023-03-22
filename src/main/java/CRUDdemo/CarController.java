package CRUDdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {

    /**
     * create a new Car
     * return a list of all the Cars
     * return a single Car (if the id is not in the db - use existsById() - returns an empty Car)
     * update the type of a specific Car, identified by id and passing a query param
     (if not present in the db, returns an empty Car)
     * delete a specific Car - if absent, the response will have a Conflict HTTP status
     * delete all the Cars in the db
     */

    public CarRepository carRepository;

    @Autowired
    public CarController(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    @PostMapping("/create-car")
    public Car createCar(@RequestBody Car inputCar){
        Car outputCar = inputCar;
        carRepository.save(outputCar);
        return outputCar;
    }

    @GetMapping("/show-cars")
    public List<Car> carsList(){
        List<Car> carsList = carRepository.findAll();
        return carsList;
    }

    @GetMapping("/find-car/{carId}")
    public Car getCarById(@PathVariable (name = "carId") long carId){
        //Optional<Car> carToFind = carRepository.findById(carId);
        if(carRepository.existsById(carId)){
            Car carToFind = carRepository.findById(carId).get();
            return carToFind;
        } else {
            return new Car();
        }
    }

    @PutMapping("/update-type/{carId}")
    public Car updateCarType(@PathVariable long carId, @RequestParam String type){
        Optional<Car> carToFind = carRepository.findById(carId);
        if(carToFind.isPresent()){
            Car carToUpdate = carToFind.get();
            carToUpdate.setType(type);
            carRepository.save(carToUpdate);
            return carToUpdate;
        } else {
            return new Car();
        }
    }

    @DeleteMapping("/deleteById/{carId}")
    public String deleteSingleCarById(@PathVariable long carId){
        if(carRepository.existsById(carId)){
            //Car carToFind = carRepository.findById(carId).get();
            carRepository.deleteById(carId);
            return "Car with id " + carId + " has been deleted";
        } else {
            throw new IllegalArgumentException("No cars with this id");
        }
    }

    /*@DeleteMapping("/deleteById-response/{carId}")
    public ResponseEntity deleteByIdWithResponse(@PathVariable long carId){
        try {
            carRepository.deleteById(carId);
            return ResponseEntity.ok("Car with id " + carId + " has been deleted!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }*/

    @DeleteMapping("/deleteById-response/{carId}")
    public ResponseEntity deleteByIdWithResponse(@PathVariable long carId){
        if(carRepository.existsById(carId)){
            carRepository.deleteById(carId);
            return ResponseEntity.ok("Car with id " + carId + " has been deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No cars with id " + carId);
        }
    }


    @DeleteMapping("/deleteAllCars")
    public String deleteAllCars(@RequestParam boolean sureToDeleteAll){
        if(sureToDeleteAll == true){
            carRepository.deleteAll();
            return "You have deleted all cars in the database!";
        } else {
            return "If you are really sure you want to delete alla cars, set \"sureToDelete\" parameter as \"true\"";
        }
    }
}
