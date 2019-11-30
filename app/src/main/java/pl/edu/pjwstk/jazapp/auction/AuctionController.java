package pl.edu.pjwstk.jazapp.auction;

import pl.edu.pjwstk.jazapp.entity.AuctionEntity;
import pl.edu.pjwstk.jazapp.entity.TestRepository;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class AuctionController {

    @Inject
    private TestRepository testRepository;

    @Inject
    private AuctionRequest auctionRequest;

    public static Collection<Part> getAllParts(Part part) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getParts().stream().filter(p -> part.getName().equals(p.getName())).collect(Collectors.toList());
    }

    public String add() throws ServletException, IOException {

        List<String> photos = new ArrayList<String>();

        Random random = new Random();
        for(Part x : getAllParts(auctionRequest.getPhotos())){
            try (InputStream input = x.getInputStream()) {
                String url = auctionRequest.getTitle()+"_"+random.nextInt();
                Files.copy(input, new File("/home/olek/Projects/jazzapp/app/src/main/content/auctionPhotos", url+".jpg").toPath());
                photos.add(url);
            }
            catch (IOException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
        testRepository.addAuction(auctionRequest.getTitle(), auctionRequest.getDescription(), auctionRequest.getPrice(), auctionRequest.getSection(), auctionRequest.getCategory(), photos);
        return "addAuction.xhtml";
    }

    public void testAjax() {
        System.out.println("dziala");
    }

    public List<AuctionEntity> getAuctions() {
        return testRepository.getAuctions();
    }
}