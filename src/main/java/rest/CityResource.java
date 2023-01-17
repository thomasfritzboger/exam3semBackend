package rest;

import dtos.CityDTO;
import dtos.FestivalDTO;
import entities.City;
import entities.Festival;
import facades.CityFacade;
import facades.FestivalFacade;
import org.glassfish.grizzly.http.util.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("cities")
public class CityResource extends Resource {

    private final CityFacade facade = CityFacade.getFacade(EMF);

    @GET
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllCities() {
        List<City> cities = facade.getAllCities();
        List<CityDTO> cityDTOs = new ArrayList<>();

        for (City city : cities) {
            cityDTOs.add(new CityDTO.Builder()
                    .setId(city.getId())
                    .setName(city.getName())
                    .setZipcode(city.getZipcode())
                    .build());
        }
        String citiesAsJson = GSON.toJson(cityDTOs);
        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(citiesAsJson).build();
    }
}
