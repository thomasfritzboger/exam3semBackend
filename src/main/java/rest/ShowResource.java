package rest;

import dtos.ShowDTO;
import entities.Show;
import facades.ShowFacade;
import org.glassfish.grizzly.http.util.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("shows")
public class ShowResource extends Resource {

    private final ShowFacade facade = ShowFacade.getFacade(EMF);

    @GET
    @RolesAllowed({"guest","admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllShows() {
        List<Show> shows = facade.getAllShows();
        List<ShowDTO> showDTOs = new ArrayList<>();

        for (Show show : shows) {
            showDTOs.add(buildStandardShowDTO(show));
        }
        String showsAsJson = GSON.toJson(showDTOs);
        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(showsAsJson).build();
    }

    @POST
    @RolesAllowed("admin")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createShow(String showFromJson) {

        ShowDTO showDTO = GSON.fromJson(showFromJson, ShowDTO.class);
        Show show = new Show(
                showDTO.getName(),
                showDTO.getDuration(),
                showDTO.getLocation(),
                showDTO.getStartDate(),
                showDTO.getStartTime()
        );

        show = facade.createShow(show);

        showDTO = buildStandardShowDTO(show);
        String showAsJson = GSON.toJson(showDTO);

        return Response.status(HttpStatus.CREATED_201.getStatusCode()).entity(showAsJson).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    public Response deleteShow(@PathParam("id") int id) {
        try {
            Show show = facade.getShowById(id);
            facade.deleteShow(show);
        } catch (EntityNotFoundException e) {
            //
        }

        return Response.noContent().build();
    }

    private ShowDTO buildStandardShowDTO(Show show) {

        return new ShowDTO.Builder()
                .setId(show.getId())
                .setName(show.getName())
                .setDuration(show.getDuration())
                .setLocation(show.getLocation())
                .setStartDate(show.getStartDate())
                .setStartTime(show.getStartTime())
                .build();
    }
}
