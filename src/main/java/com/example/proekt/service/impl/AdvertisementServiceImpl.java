package com.example.proekt.service.impl;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.AdvertisementType;
import com.example.proekt.model.Apartment;
import com.example.proekt.model.MunicipalityType;
import com.example.proekt.model.exceptions.InvalidAddIdException;
import com.example.proekt.repository.AdvertisementRepository;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.ApartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final ApartmentService apartmentService;

    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository, ApartmentService apartmentService) {
        this.advertisementRepository = advertisementRepository;
        this.apartmentService = apartmentService;
    }

    @Override
    public List<Advertisement> listAll() {
        return advertisementRepository.findAll();
    }

    @Override
    public Advertisement findById(Long id) {
        return advertisementRepository.findById(id).orElseThrow(InvalidAddIdException::new);
    }

    @Override
    public Advertisement create(Long apartmentID, AdvertisementType type, Double price) {
        Apartment apartment= apartmentService.findById(apartmentID);
        return advertisementRepository.save(new Advertisement(apartment,type, price));
    }

    @Override
    public Advertisement update(Long id,Long apartmentID, AdvertisementType type, Double price) {
        Apartment apartment= apartmentService.findById(apartmentID);
        Advertisement advertisement= this.findById(id);
        advertisement.setApartment(apartment);
        advertisement.setType(type);
        advertisement.setPrice(price);
        return advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement addRating(Double rating, Long id) {
        Advertisement advertisement= this.findById(id);
        List<Double> ratings= advertisement.getRatings();
        ratings.add(rating);
        advertisement.setRatings(ratings);
        return advertisementRepository.save(advertisement);
    }

    @Override
    public Double ratingAvg(Long id) {
        Advertisement advertisement= this.findById(id);
        List<Double> ratings= advertisement.getRatings();
        Double avg= ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return avg;
    }

    @Override
    public Integer numbComments(Long id) {
        Advertisement advertisement= this.findById(id);
        List<String> comments= advertisement.getComments();
        Integer numb= comments.size();
        return  numb;
    }

    @Override
    public Advertisement addComment(String comment, Long id) {
        Advertisement advertisement= this.findById(id);
        List<String> comments= advertisement.getComments();
        comments.add(comment);
        advertisement.setComments(comments);
        return advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement delete(Long id) {
        Advertisement advertisement= this.findById(id);
        advertisementRepository.delete(advertisement);
        return advertisement;
    }

    @Override
    public List<Advertisement> filter(Double priceMore, Double priceLess, MunicipalityType municipality, Double avgRatingMore,
                                      Double avgRatingLess, Double comments, Integer numRooms, Integer sizeMore, Integer sizeLess, AdvertisementType advertisementType) {
        List<Advertisement> filteredAds = this.listAll();


        if (priceMore != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> ad.getPrice() > priceMore)
                    .collect(Collectors.toList());
        }

        if (priceLess != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> ad.getPrice() < priceLess)
                    .collect(Collectors.toList());
        }

        if (municipality !=null) {
            filteredAds = filteredAds.stream()
                    .filter(ad-> ad.getApartment().getMunicipality().equals(municipality)).collect(Collectors.toList());
        }

        if (avgRatingMore != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> this.ratingAvg(ad.getId()) >= avgRatingMore)
                    .collect(Collectors.toList());
        }

        if (avgRatingLess != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> this.ratingAvg(ad.getId())<= avgRatingLess)
                    .collect(Collectors.toList());
        }

        if (numRooms != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> Objects.equals(ad.getApartment().getNumRooms(), numRooms))
                    .collect(Collectors.toList());
        }

        if (sizeMore != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad -> ad.getApartment().getSize() >= sizeMore)
                    .collect(Collectors.toList());
        }

        if (sizeLess != null) {
            filteredAds = filteredAds.stream()
                    .filter(ad ->  ad.getApartment().getSize() < sizeLess)
                    .collect(Collectors.toList());
        }
        if (advertisementType !=null) {
            filteredAds = filteredAds.stream()
                    .filter(ad-> ad.getType().equals(advertisementType)).collect(Collectors.toList());
        }
        return filteredAds;
    }

}
