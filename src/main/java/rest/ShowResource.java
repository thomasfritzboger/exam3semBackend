package rest;

import dtos.ShowDTO;
import entities.Show;
import facades.ShowFacade;
import org.glassfish.grizzly.http.util.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("shows")
public class ShowResource extends Resource {

    private final ShowFacade facade = ShowFacade.getFacade(EMF);

    @GET
    @RolesAllowed("guest")
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

    private ShowDTO buildStandardShowDTO(Show show) {

        return new ShowDTO.Builder()
                .setId(show.getId())
                .setName(show.getName())
                .setDuration(show.getDuration())
                .setLocation(show.getLocation())
                .setStartDate(show.getStartdate())
                .setStartTime(show.getStarttime())
                .build();
    }
}
