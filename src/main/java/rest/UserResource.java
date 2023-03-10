package rest;

import dtos.*;
import entities.Festival;
import entities.User;
import errorhandling.InvalidPasswordException;
import errorhandling.InvalidUsernameException;
import errorhandling.UniqueException;
import facades.FestivalFacade;
import facades.RoleFacade;
import facades.UserFacade;
import org.glassfish.grizzly.http.util.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("users")
public class UserResource extends Resource {
    private final UserFacade facade = UserFacade.getFacade(EMF);
    private final RoleFacade roleFacade = RoleFacade.getFacade(EMF);
    private final FestivalFacade festivalFacade = FestivalFacade.getFacade(EMF);

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createUser(String userFromJson) {
        UserDTO userDTO = GSON.fromJson(userFromJson, UserDTO.class);
        User user;
        Festival festival;

        try {
            festival = festivalFacade.getFestivalById(userDTO.getFestival().getId());

            user = new User(
                    userDTO.getPassword(),
                    userDTO.getUsername(),
                    userDTO.getName(),
                    userDTO.getPhone(),
                    userDTO.getEmail(),
                    festival
            );

            user.addRole(roleFacade.getRoleByRole("guest"));
            user = facade.createUser(user);
        } catch (UniqueException e) {
             throw new WebApplicationException(e.getMessage(),HttpStatus.CONFLICT_409.getStatusCode());
        }
        catch (InvalidUsernameException | InvalidPasswordException e) {
            throw new BadRequestException(e.getMessage());
        }
        //Build af standardUserDTO flyttet ud i egen metode for læselighed
        userDTO = buildStandardUserDTO(user);
        String userToJson = GSON.toJson(userDTO);
        return Response.status(HttpStatus.CREATED_201.getStatusCode()).entity(userToJson).build();
    }

    @GET
    @RolesAllowed({"guest","admin"})
    @Path("me")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMe() {
        User user;
        int id = Integer.parseInt(securityContext.getUserPrincipal().getName());
        try {
            user = facade.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("No such user with id " + id + " exist");
        }
        UserDTO userDTO = buildStandardUserDTO(user);

        String userToJson = GSON.toJson(userDTO);
        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(userToJson).build();
    }

    @GET
    @RolesAllowed("admin")
    @Produces ({MediaType.APPLICATION_JSON})
    public Response getAllUsers() {
        List<User> allUsers = facade.getAllUsers();
        List<UserDTO> allUserDTOs = new ArrayList<>();
        for (User user : allUsers) {

            allUserDTOs.add(new UserDTO.Builder()
                    .setId(user.getId())
                    .setUsername(user.getUsername())
                    .setRoles(user.getRolesAsStringList())
                    .build());
        }
        String userDtosToJson = GSON.toJson(allUserDTOs);
        return Response.status(HttpStatus.OK_200.getStatusCode()).entity(userDtosToJson).build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateUser(String userFromJson, @PathParam("id") int id) {
        UserDTO userDTO = GSON.fromJson(userFromJson, UserDTO.class);
        User user;
        try {
            user = facade.getUserById(id);

            if (userDTO.getUsername() != null) {
                user.setUsername(userDTO.getUsername());
            }
            facade.updateUser(user);

        }catch (EntityNotFoundException entityNotFoundException){
            throw new BadRequestException("User does not exist");
        } catch (UniqueException uniqueException) {
            throw new WebApplicationException("Chosen username is already in use",
                    HttpStatus.CONFLICT_409.getStatusCode());
        } catch (InvalidUsernameException invalidUsernameException) {
            throw new WebApplicationException("Your username was either too long or too short, " +
                    "it should be between 3 and 20 characters",
                    HttpStatus.CONFLICT_409.getStatusCode());
        }
        UserDTO updatedUserDTO = buildStandardUserDTO(user);
        return Response.ok().entity(GSON.toJson(updatedUserDTO)).build();
    }

    @DELETE
    @RolesAllowed("admin")
    @Path("{id}")
    public Response deletePerson(@PathParam("id") int id) {
        try {
            facade.deleteUser(id);
        } catch (EntityNotFoundException exception) {
            //
        }
        return Response.noContent().build();
    }

    private UserDTO buildStandardUserDTO(User user) {
        FestivalDTO festivalDTO = new FestivalDTO.Builder()
                .setId(user.getFestival().getId())
                .build();

        return new UserDTO.Builder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setPhone(user.getPhone())
                .setEmail(user.getEmail())
                .setFestivalDTO(festivalDTO)
                .setRoles(user.getRolesAsStringList())
                .build();
    }
}
