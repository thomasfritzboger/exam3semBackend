package rest;

import dtos.CityDTO;
import dtos.FestivalDTO;
import dtos.ShowDTO;
import entities.City;
import entities.Festival;
import entities.Show;
import facades.CityFacade;
import facades.FestivalFacade;
import org.glassfish.grizzly.http.util.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Path("festivals")
public class FestivalResource extends Resource {

    private final FestivalFacade facade = FestivalFacade.getFacade(EMF);
    private final CityFacade cityFacade = CityFacade.getFacade(EMF);

    @GET
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllShows() {
        List<Festival> festivals = facade.getAllFestivals();
        List<FestivalDTO> festivalDTOs = new ArrayList<>();

        for (Festival festival : festivals) {
            festivalDTOs.add(buildStandardFestivalDTO(festival));
        }
        String festivalsAsJson = GSON.toJson(festivalDTOs);
        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(festivalsAsJson).build();
    }

    @Path("{id}")
    @PUT
    @RolesAllowed("admin")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateFestival(@PathParam("id")int id, String festivalFromJson) {
        FestivalDTO festivalDTO = GSON.fromJson(festivalFromJson, FestivalDTO.class);

        City city;
        Festival festival;
        try {
            festival = facade.getFestivalById(id);
            city = cityFacade.getCityById(festivalDTO.getCity().getId());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException();
        }

        festival.setName(festivalDTO.getName());
        festival.setDuration(festivalDTO.getDuration());
        festival.setStartDate(LocalDate.parse(festivalDTO.getStartDate()));
        festival.setCity(city);

        facade.updateFestival(festival);

        festivalDTO = buildStandardFestivalDTO(festival);
        String festivalAsJson = GSON.toJson(festivalDTO);

        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(festivalAsJson).build();
    }

    private FestivalDTO buildStandardFestivalDTO(Festival festival) {
        CityDTO cityDTO = new CityDTO.Builder()
                .setId(festival.getCity().getId())
                .setName(festival.getCity().getName())
                .setZipcode(festival.getCity().getZipcode())
                .build();

        return new FestivalDTO.Builder()
                .setId(festival.getId())
                .setName(festival.getName())
                .setDuration(festival.getDuration())
                .setStartDate(festival.getStartDate())
                .setCityDTO(cityDTO)
                .build();
    }

}
