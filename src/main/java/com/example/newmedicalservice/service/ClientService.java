package com.example.newmedicalservice.service;

import com.example.newmedicalservice.dto.*;
import com.example.newmedicalservice.dtoForAnswers.*;
import com.example.newmedicalservice.repository.*;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class ClientService {

    private static final Logger LOGGER = LogManager.getLogger(ClientService.class);
    private static final Marker USER = MarkerManager.getMarker("USER");
    private static final Marker ADMIN = MarkerManager.getMarker("ADMIN");

    private ClientRepository clientRepository;
    private FamilyRepository familyRepository;
    private ClientDocsRepository clientDocsRepository;
    private ImageTemplatesRepositories imageTemplatesRepositories;
    private DoctorRepository doctorRepository;
    private DoctorArchiveRepository doctorArchiveRepository;
    private UserRepository userRepository;
    private AssignmentRepository assignmentRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, FamilyRepository familyRepository,
                         ClientDocsRepository clientDocsRepository, ImageTemplatesRepositories imageTemplatesRepositories,
                         DoctorRepository doctorRepository, DoctorArchiveRepository doctorArchiveRepository,
                         UserRepository userRepository, AssignmentRepository assignmentRepository) {
        this.clientRepository = clientRepository;
        this.familyRepository = familyRepository;
        this.clientDocsRepository = clientDocsRepository;
        this.imageTemplatesRepositories = imageTemplatesRepositories;
        this.doctorRepository = doctorRepository;
        this.doctorArchiveRepository = doctorArchiveRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }


    @Value("${templates.contract}")
    private String clientContract;

    @Value("${templates.agreement}")
    private String clientAgreement;

    @Value("${templates.questionnaire}")
    private String clientQuestionnaire;


//    public boolean fillImageDocumentTemplates() {
//        boolean result = Boolean.parseBoolean(null);
//        ImageTemplatesClientDocuments imageTemplatesClientDocuments = new ImageTemplatesClientDocuments();
//        imageTemplatesClientDocuments.setTemplateContract(pdfFieldToByte(clientContract));
//        imageTemplatesClientDocuments.setTemplateAgreement(pdfFieldToByte(clientAgreement));
//        imageTemplatesClientDocuments.setTemplateQuestionnaire(pdfFieldToByte(clientQuestionnaire));
//        imageTemplatesClientDocuments.setTemplateClientFoto(null);
//        imageTemplatesRepositories.save(imageTemplatesClientDocuments);
//
//        ImageTemplatesClientDocuments imageTemplatesClientDocuments1 = imageTemplatesRepositories.getReferenceById(1L);
//        if (imageTemplatesClientDocuments1 != null) {
//            result = true;
//        }
//        return result;
//    }
//
//    public byte[] pdfFieldToByte(String pathToPDF) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
//        PdfReader reader = null;
//        try {
//            reader = new PdfReader(pathToPDF);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        PdfDocument pdfDocument = new PdfDocument(reader, writer);
//        Document document = new Document(pdfDocument);
//        document.close();
//        byte[] pdfDocToByte = byteArrayOutputStream.toByteArray();
//        return pdfDocToByte;
//    }


    public ClientDocsDTO setUpClientData(String clientID,
                                         MultipartFile clientFoto,
                                         MultipartFile contract,
                                         MultipartFile agreement,
                                         MultipartFile questionnaire) throws IOException {

        ClientDocs clientDocs = null;
        Client client = clientRepository.getReferenceById(clientID);
        Long idClientDocs = client.getClientDocs().getId();
        Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);

        if (clientDocsOptional.isPresent()) {
            clientDocs = clientDocsOptional.get();
        }

        if (clientFoto != null) {
            clientDocs.setClientFoto(clientFoto.getBytes());
        }

        if (contract != null) {
            clientDocs.setContract(contract.getBytes());
        }

        if (agreement != null) {
            clientDocs.setAgreement(agreement.getBytes());
        }

//        if (questionnaire != null) {
//            clientDocs.setQuestionnaire(questionnaire.getBytes());
//        }

        client.setClientDocs(clientDocs);
        clientRepository.save(client);

        // check ClientDocs are full
        ClientDocsDTO clientDocsDTO = new ClientDocsDTO();
        ClientDocs clientDocsAfter = null;
        Optional<ClientDocs> clientDocsOptionalAfter = clientDocsRepository.findById(idClientDocs);

        if (clientDocsOptionalAfter.isPresent()) {
            clientDocsAfter = clientDocsOptional.get();
        }
        clientDocsDTO.setId(clientDocsAfter.getId());
        clientDocsDTO.setClientID(clientDocsAfter.getClient().getId());
        clientDocsDTO.setPassportNumber(clientDocsAfter.getPassportNumber());

        if (clientDocsAfter.getContract() != null) {
            clientDocsDTO.setContract(true);
        }
        if (clientDocsAfter.getAgreement() != null) {
            clientDocsDTO.setAgreement(true);
        }
        if (clientDocsAfter.getQuestionnaire() != null) {
            clientDocsDTO.setQuestionnaire(true);
        }
        if (clientDocsAfter.getClientFoto() != null) {
            clientDocsDTO.setClientFoto(true);
        }

        return clientDocsDTO;
    }

    public List<ClientDTO> searchClients(String id, String passportNumber, String telephon, String email, String surName) {

        List<Client> selectedClients = new ArrayList<>();
        List<ClientDTO> foundСlients = new ArrayList<>();
        if (id != null) {
            Client client = clientRepository.getReferenceById(id);
            selectedClients.add(client);
        } else if (passportNumber != null) {
            Optional<Client> client = clientRepository.findByPassportNumber(passportNumber);
            if (client.isPresent()) {
                selectedClients.add(client.get());
            }
        } else if (telephon != null) {
            List<Client> clients = clientRepository.findByTelephone(telephon);
            for (Client client : clients) {
                selectedClients.add(client);
            }
        } else if (email != null) {
            List<Client> clients = clientRepository.findByEmail(email);
            for (Client client : clients) {
                selectedClients.add(client);
            }
        } else if (surName != null) {
            List<Client> clients = clientRepository.findBySurName(surName);
            for (Client client : clients) {
                selectedClients.add(client);
            }
        } else {
            List<Client> clients = clientRepository.findAll();
            for (Client client : clients) {
                selectedClients.add(client);
            }
        }

        for (Client client : selectedClients) {
            ClientDTO clientDTO = mapClient_toClientDTOclass(client);
            foundСlients.add(clientDTO);
        }
        return foundСlients;
    }

    public List<FamilyDTO> getFamilyList() {
        List<FamilyDTO> familyDTOList = new ArrayList<>();

        List<Family> familyList = familyRepository.findAll();
        for (Family family : familyList) {
            List<ClientDTO> clientDTOList = new ArrayList<>();
            FamilyDTO familyDTO = new FamilyDTO();
            familyDTO.setId(family.getId());

            List<Client> clientListfromFamily = family.getClientList();
            for (Client client : clientListfromFamily) {
                ClientDTO clientDTO = mapClient_toClientDTOclass(client);
                clientDTOList.add(clientDTO);
            }
            familyDTO.setClientList(clientDTOList);
            familyDTO.setFamilyName(family.getFamilyName());
            familyDTO.setFamilyHead(family.getFamilyHead());
            familyDTO.setFamilyMobile(family.getFamilyMobile());
            familyDTO.setDescription(family.getDescription());
            familyDTOList.add(familyDTO);
        }
        return familyDTOList;
    }

//====================   pdf processing - first option   =======================================>
//
//    public byte[] restoreContract_fromDB(String clientID) throws IOException {
//        ImageTemplatesClientDocuments imageClientDocuments = imageTemplatesRepositories.getReferenceById(1L);
//        byte[] contractByte = imageClientDocuments.getTemplateContract();
//        String sourceFile = "templateClientContract.pdf";
//        OutputStream out = new FileOutputStream(sourceFile);
//        out.write(contractByte);
//        out.close();
//
//        String destinationFile = replaceNameContentFromPDF(sourceFile, clientID);
//        //String fileAfterInsertName = replaceNameContentFromPDF(sourceFile, clientID);
//        //String fileAfterInsertPassportNumber = replacePassportNumberContentFromPDF(fileAfterInsertName, clientID);
//        String finalTemplateContract = replacePassportNumberContentFromPDF(destinationFile, clientID);
//
//        Path pdfPath = Paths.get(finalTemplateContract);
//        byte[] customizedContractByte = Files.readAllBytes(pdfPath);
//        return customizedContractByte;
//    }
//
//    private String replacePassportNumberContentFromPDF(String sourceFile, String clientID) {
//        String destinationFile = "finalTemplateClientContract.pdf";
//        String searchText = "Получатель";
//        Client client = clientRepository.getReferenceById(clientID);
//        String insertText = clientRepository.getReferenceById(clientID).getPassportNumber();
//        PdfDocument pdfDocument = null;
//        try {
//            PdfReader reader = new PdfReader(sourceFile);
//            PdfWriter writer = new PdfWriter(destinationFile);
//            pdfDocument = new PdfDocument(reader, writer);
//            replaceTextContentFromDocument(pdfDocument, searchText, insertText);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            log.error("The error has occurred ==> " + ioe);
//        }
//        pdfDocument.close();
//        return destinationFile;
//    }
//
//    private String replaceNameContentFromPDF(String sourceFile, String clientID) {
//        String destinationFile = "client" + clientID + "_" + sourceFile;
//        String searchText = "[clientName]";
//        Client client = clientRepository.getReferenceById(clientID);
//        String firstName = client.getFirstName();
//        String lastName = client.getLastName();
//        String surname = client.getSurName();
//        String insertText = firstName + " " + lastName + " " + surname;
//        PdfDocument pdfDocument = null;
//        try {
//            PdfReader reader = new PdfReader(sourceFile);
//            PdfWriter writer = new PdfWriter(destinationFile);
//            pdfDocument = new PdfDocument(reader, writer);
//            replaceTextContentFromDocument(pdfDocument, searchText, insertText);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            log.error("The error has occurred ==> " + ioe);
//        }
//        pdfDocument.close();
//        return destinationFile;
//    }
//
//    private void replaceTextContentFromDocument(PdfDocument pdfDocument, String searchText, String insertText) throws IOException {
//        CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
//        strategy.add(new RegexBasedCleanupStrategy(searchText).setRedactionColor(ColorConstants.WHITE));
//        PdfCleaner.autoSweepCleanUp(pdfDocument, strategy);
//
//        for (IPdfTextLocation location : strategy.getResultantLocations()) {
//            PdfPage page = pdfDocument.getPage(location.getPageNumber() + 1);
//            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), page.getDocument());
//            Canvas canvas = new Canvas(pdfCanvas, location.getRectangle());
//            canvas.add(new Paragraph(insertText).setFontSize(9).setMarginTop(0f));
//        }
//    }

    //====================   pdf processing - first option - end   =======================================<

    public Client redactClientData(String clientPassportNumber, String clientKxNumber, String clientFirstName,
                                   String clientLastName, String clientSurName, String clientEmail, String clientTelephon,
                                   String clientAddress, String clientServiceDescription, String clientFamilyID, LocalDateTime clientStartPaymentDate,
                                   LocalDateTime clientStartServiceDate, Boolean clientBlocked,
                                   String clientBlockedReasonDescription, LocalDateTime clientBlockDate,
                                   String clientID_Doctor) {

        Marker marker = getLogMarker();
        Optional<Client> optionalClient = clientRepository.findByPassportNumber(clientPassportNumber);
        Client client = null;
        if (optionalClient.isPresent()) {
            client = optionalClient.get();

            if (clientFirstName != null) {
                client.setFirstName(clientFirstName);
            }
            if (clientLastName != null) {
                client.setLastName(clientLastName);
            }
            if (clientSurName != null) {
                client.setSurName(clientSurName);
            }
            if (clientKxNumber != null) {
                client.setKxNumber(clientKxNumber);
            }
            if (clientEmail != null) {
                client.setEmail(clientEmail);
            }
            if (clientTelephon != null) {
                client.setTelephone(clientTelephon);
            }
            if(clientAddress != null){
                client.setAddress(clientAddress);
            }
            if (clientServiceDescription != null) {
                client.setServiceDescription(clientServiceDescription);
            }
            if (clientFamilyID != null) {
                client.setFamily(familyRepository.getReferenceById(Integer.valueOf(clientFamilyID)));
            }
            if (clientStartPaymentDate != null) {
                client.setStartPaymentDate(clientStartPaymentDate);
            }
            if (clientStartServiceDate != null) {
                client.setStartServiceDate(clientStartServiceDate);
            }
            if (clientBlocked != null) {
                client.setBlocked(clientBlocked);
            }
            if (clientBlockedReasonDescription != null) {
                client.setBlockedReasonDescription(clientBlockedReasonDescription);
            }
            if (clientBlockDate != null) {
                client.setBlockDate(clientBlockDate);
            }
            if(clientBlockDate == null && clientBlocked == null){
                client.setBlockDate(clientBlockDate);
                client.setBlocked(false);
            }
            if (clientID_Doctor != null) {
                Doctor doctor = null;
                Integer clientID_DoctorInt = null;
                try {
                    clientID_DoctorInt = Integer.valueOf(clientID_Doctor);
                } catch (NumberFormatException nfe) {
                    LOGGER.warn(marker, "clientID_Doctor is not defined: " + nfe);
                }
                if (clientID_DoctorInt != null) {
                    Optional<Doctor> doctorOptional = doctorRepository.findById(clientID_DoctorInt);
                    if (doctorOptional.isPresent()) {
                        doctor = doctorOptional.get();
                        client.setDoctor(doctor);
                        /* archive the doctor's assignment data */
                        DoctorArchive doctorArchive = new DoctorArchive();
                        doctorArchive.setIdDoctor(doctor.getId());
                        doctorArchive.setIdClient(client.getId());
                        doctorArchive.setSetupDate(LocalDateTime.now());
                        doctorArchiveRepository.save(doctorArchive);
                    } else {
                        LOGGER.error(marker, "Doctor with ID = '" + clientID_Doctor + "' does not exist in the database");
                    }
                }
            }
            clientRepository.save(client);
            LOGGER.info(marker, "Client with ID = '" + client.getId() + "' changed by user: '" + marker.getName() + "'");
        } else {
            LOGGER.error(marker, "When trying to edit a client with a passport number of '"
                    + clientPassportNumber + "' the client is found not to exist");
        }
        return client;
    }


    public ClientDTO mapClient_toClientDTOclass(@NotNull Client client) {
        List<AssignmentDTO> assignmentDTOList = new ArrayList<>();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setPassportNumber(client.getPassportNumber());
        clientDTO.setFirstName(client.getFirstName());
        clientDTO.setSurName(client.getSurName());
        clientDTO.setLastName(client.getLastName());
        clientDTO.setRegistrationDate(client.getRegistrationDate());
        if (client.getFamily() != null) {
            clientDTO.setFamilyID(client.getFamily().getId());
        }
        clientDTO.setTelephone(client.getTelephone());
        clientDTO.setEmail(client.getEmail());
        clientDTO.setAddress(client.getAddress());
        clientDTO.setStartPaymentDate(client.getStartPaymentDate());
        clientDTO.setStartServiceDate(client.getStartServiceDate());
        clientDTO.setServiceDescription(client.getServiceDescription());
        clientDTO.setBlocked(client.getBlocked());
        clientDTO.setBlockedReasonDescription(client.getBlockedReasonDescription());
        clientDTO.setBlockDate(client.getBlockDate());
        if (client.getClientDocs() != null) {
            clientDTO.setClientDocsID(client.getClientDocs().getId());
        }
        clientDTO.setKxNumber(client.getKxNumber());
        if (client.getDoctor() != null) {
            clientDTO.setDoctorID(client.getDoctor().getId());
        }

        List<Assignment> assignmentList = client.getAssignmentList();
        if (assignmentList != null) {
            for (Assignment assignment : assignmentList) {
                AssignmentDTO assignmentDTO = mapAssignment_toAssignmentDTO(assignment);
                assignmentDTOList.add(assignmentDTO);
            }
            clientDTO.setAssignmentList(assignmentDTOList);
        }
        return clientDTO;
    }


    public FamilyDTO mapFamily_toFamilyDTO(@NotNull Family family) {
        FamilyDTO familyDTO = new FamilyDTO();
        List<ClientDTO> clientDTOList = new ArrayList<>();
        familyDTO.setId(family.getId());
        familyDTO.setFamilyName(family.getFamilyName());
        familyDTO.setFamilyHead(family.getFamilyHead());
        familyDTO.setFamilyMobile(family.getFamilyMobile());
        familyDTO.setDescription(family.getDescription());

        List<Client> clientListFromFamily = family.getClientList();
        for (Client client : clientListFromFamily) {
            ClientDTO clientDTO = mapClient_toClientDTOclass(client);
            clientDTOList.add(clientDTO);
        }

        familyDTO.setClientList(clientDTOList);
        return familyDTO;
    }


    public Doctor createNewDoctor(DoctorDTO doctorDTO) {
        Marker marker = getLogMarker();
        Doctor newDoctor = new Doctor();
        try {
            newDoctor.setDoctorFirstName(doctorDTO.getDoctorFirstName());
            newDoctor.setDoctorLastName(doctorDTO.getDoctorLastName());
            newDoctor.setDoctorSureName(doctorDTO.getDoctorSureName());
            newDoctor.setDoctorTelefon(doctorDTO.getDoctorTelefon());
            newDoctor.setDoctorEmail(doctorDTO.getDoctorEmail());
            newDoctor.setDoctorAddres(doctorDTO.getDoctorAddres());
            newDoctor.setDescription(doctorDTO.getDescription());
            newDoctor.setDoctorType(Doctor.DoctorType.valueOf(doctorDTO.getDoctorType()));
            doctorRepository.save(newDoctor);
            LOGGER.info(marker, "The 'Doctor' object was successfully created: " + newDoctor.toString());
        } catch (Exception e) {
            LOGGER.error(marker, "An error occurred when creating the 'Doctor' object ==> " + e);
        }
        return newDoctor;
    }


    public DoctorDTO mapDoctor_toDoctorDTO(Doctor newDoctor) {
        Marker marker = getLogMarker();
        List<ClientDTO> clientDTOList = new ArrayList<>();
        List<AssignmentDTO> assignmentDTOList = new ArrayList<>();
        DoctorDTO doctorDTO = new DoctorDTO();

        try {
            doctorDTO.setId(newDoctor.getId());
            doctorDTO.setDoctorFirstName(newDoctor.getDoctorFirstName());
            doctorDTO.setDoctorLastName(newDoctor.getDoctorLastName());
            doctorDTO.setDoctorSureName(newDoctor.getDoctorSureName());
            doctorDTO.setDoctorTelefon(newDoctor.getDoctorTelefon());
            doctorDTO.setDoctorEmail(newDoctor.getDoctorEmail());
            doctorDTO.setDoctorAddres(newDoctor.getDoctorAddres());
            doctorDTO.setDescription(newDoctor.getDescription());
            doctorDTO.setDoctorType(newDoctor.getDoctorType().name());

            List<Client> clientListFromDoctor = newDoctor.getClientList();
            if (clientListFromDoctor != null) {
                for (Client client : clientListFromDoctor) {
                    ClientDTO clientDTO = mapClient_toClientDTOclass(client);
                    clientDTOList.add(clientDTO);
                }
                doctorDTO.setClientList(clientDTOList);
            }

            List<Assignment> assignmentListFromDoctor = newDoctor.getAssignmentList();
            if (assignmentListFromDoctor != null) {
                for (Assignment assignment : assignmentListFromDoctor) {
                    AssignmentDTO assignmentDTO = mapAssignment_toAssignmentDTO(assignment);
                    assignmentDTOList.add(assignmentDTO);
                }
                doctorDTO.setAssignmentList(assignmentDTOList);
            }
        } catch (Exception e) {
            LOGGER.error(marker, "An error has occurred in the 'mapDoctor_toDoctorDTO' method ==> " + e);
        }
        return doctorDTO;
    }

    public Marker getLogMarker() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserRole = authentication.getAuthorities().toString();
        Marker marker = null;
        if (currentUserRole.toLowerCase().contains("user")) {
            marker = USER;
        } else if (currentUserRole.toLowerCase().contains("admin")) {
            marker = ADMIN;
        } else {
            log.error("user logged in is not defined");
        }
        return marker;
    }


    public List<DoctorDTO> findAllDoctors() {
        List<DoctorDTO> doctorDTOList = new ArrayList<>();
        List<Doctor> doctorList = doctorRepository.findAll();
        if (doctorList != null) {
            for (Doctor doctor : doctorList) {
                DoctorDTO doctorDTO = mapDoctor_toDoctorDTO(doctor);
                doctorDTOList.add(doctorDTO);
            }
        }
        return doctorDTOList;
    }

    public List<ClientDTO> getAllClients() {
        List<Client> allClients = new ArrayList<>();
        List<ClientDTO> allClientsDTO = new ArrayList<>();
        allClients = clientRepository.findAll();
        if (!allClients.isEmpty()) {
            for (Client client : allClients) {
                ClientDTO clientDTO = mapClient_toClientDTOclass(client);
                allClientsDTO.add(clientDTO);
            }
        }
        return allClientsDTO;
    }

    public List<String> getDoctorType() {
        List<String> doctorTypes = new ArrayList<>();
        Doctor.DoctorType[] doctorTypesArray = Doctor.DoctorType.values();
        List<Doctor.DoctorType> listOfDoctorType = Arrays.asList(doctorTypesArray);
        for (Doctor.DoctorType ddt : listOfDoctorType) {
            String type = ddt.name();
            doctorTypes.add(type);
        }
        return doctorTypes;
    }


    public Doctor redactDoctor(String doctorID, String doctorFirstName, String doctorLastName, String doctorSureName,
                               String doctorTelefon, String doctorEmail, String doctorAddres, String description,
                               String doctorType) {
        Marker marker = getLogMarker();
        Optional<Doctor> optionalDoctor = doctorRepository.findById(Integer.valueOf(doctorID));
        Doctor doctor = null;
        try {
            if (optionalDoctor.isPresent()) {
                doctor = optionalDoctor.get();

                if (!doctorFirstName.isEmpty()) {
                    doctor.setDoctorFirstName(doctorFirstName);
                }
                if (!doctorLastName.isEmpty()) {
                    doctor.setDoctorLastName(doctorLastName);
                }
                if (!doctorSureName.isEmpty()) {
                    doctor.setDoctorSureName(doctorSureName);
                }
                if (!doctorTelefon.isEmpty()) {
                    doctor.setDoctorTelefon(doctorTelefon);
                }
                if (!doctorEmail.isEmpty()) {
                    doctor.setDoctorEmail(doctorEmail);
                }
                if (!doctorAddres.isEmpty()) {
                    doctor.setDoctorAddres(doctorAddres);
                }
                if (!description.isEmpty()) {
                    doctor.setDescription(description);
                }
                if (!doctorType.isEmpty()) {
                    try {
                        Doctor.DoctorType typeEnumDoc = Doctor.DoctorType.valueOf(doctorType);
                        doctor.setDoctorType(typeEnumDoc);
                    } catch (IllegalArgumentException iae) {
                        LOGGER.error(marker, "No enum constant com.example.medicalservice.dto.Doctor.DoctorType." + doctorType);
                        LOGGER.error(marker, "Error received ==> " + iae);
                    }
                }
                doctorRepository.save(doctor);
                LOGGER.info(marker, " The object 'Doctor' has been successfully edited: " + doctor.toString());
            } else {
                LOGGER.error(marker, "Attempt to edit a doctor with ID = '" + doctorID +
                        "' failed because the doctor with this ID does not exist in the database");
            }
        } catch (Exception e) {
            LOGGER.error(marker, "Error received ==> " + e);
        }
        return doctor;
    }


    public Family redactFamilyData(String familyID, String familyName, String familyMobile,
                                   String familyDescription, String familyHead) {

        Marker marker = getLogMarker();
        Optional<Family> optionalFamily = familyRepository.findById(Integer.valueOf(familyID));
        Family familyForModify = null;
        if (optionalFamily.isPresent()) {
            familyForModify = optionalFamily.get();
            if (!familyName.isEmpty()) {
                familyForModify.setFamilyName(familyName);
            }
            if (!familyMobile.isEmpty()) {
                familyForModify.setFamilyMobile(familyMobile);
            }
            if (!familyDescription.isEmpty()) {
                familyForModify.setDescription(familyDescription);
            }
            if (!familyHead.isEmpty()) {
                familyForModify.setFamilyHead(familyHead);
            }
            familyRepository.save(familyForModify);
            LOGGER.info(marker, "Changes were made to the family's setting data with familyID = " + familyID);
        } else {
            LOGGER.error(marker, "Attempt to edit a Family with ID = '" + familyID +
                    "' failed because the Family with this ID does not exist in the database");
        }
        return familyForModify;
    }


    public Assignment createNewAssignment(AssignmentDTO assignmentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName_inService = authentication.getName();
        Marker marker = getLogMarker();
        Integer idUser = null;
        try {
            idUser = userRepository.findByLogin(currentUserName_inService).getId();
        } catch (Exception e) {
            LOGGER.error(marker, "An error occurred when searching for a user by name: '"
                    + currentUserName_inService + "' ==> " + e);
        }

        Assignment newAssignment = new Assignment();
        try {
            newAssignment.setCheckupAddress(assignmentDTO.getCheckupAddress());
            newAssignment.setCheckupMobile(assignmentDTO.getCheckupMobile());
            newAssignment.setCheckupEmail(assignmentDTO.getCheckupEmail());
            newAssignment.setCheckupDescription(assignmentDTO.getCheckupDescription());
            newAssignment.setDateTimeAppointment(LocalDateTime.now());
            newAssignment.setDateTimeWhenToDo(assignmentDTO.getDateTimeWhenToDo());
            newAssignment.setAssignmentDescription(assignmentDTO.getAssignmentDescription());
            newAssignment.setIdUser(idUser);

            Optional<Doctor> doctorOptional = doctorRepository.findById(Integer.valueOf(assignmentDTO.getDoctorId()));
            if (doctorOptional.isPresent()) {
                newAssignment.setDoctor(doctorOptional.get());
            }

            //    Client currentClient = clientRepository.findByPassportNumber()

            Optional<Client> clientOptional = clientRepository.findById(assignmentDTO.getClientId());
            if (clientOptional.isPresent()) {
                newAssignment.setClient(clientOptional.get());
            }

            newAssignment.setAssignmentType(Assignment.AssignmentType.valueOf(assignmentDTO.getAssignmentType()));
            newAssignment.setIsDone(assignmentDTO.getIsDone());
            assignmentRepository.save(newAssignment);
            LOGGER.info(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
            LOGGER.info(marker, "The 'Assignment' object was successfully created with ID = " + newAssignment.getId());

        } catch (Exception e) {
            LOGGER.info(marker, "There is currently a user in the service: '" + currentUserName_inService + "'");
            LOGGER.error(marker, "An error occurred when creating the 'Assignment' object ==> " + e);
        }
        return newAssignment;
    }


    public AssignmentDTO mapAssignment_toAssignmentDTO(Assignment assignment) {
        Marker marker = getLogMarker();
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        try {
            assignmentDTO.setAssignmentId(String.valueOf(assignment.getId()));
            assignmentDTO.setCheckupAddress(assignment.getCheckupAddress());
            assignmentDTO.setCheckupMobile(assignment.getCheckupMobile());
            assignmentDTO.setCheckupEmail(assignment.getCheckupEmail());
            assignmentDTO.setCheckupDescription(assignment.getCheckupDescription());
            assignmentDTO.setDateTimeAppointment(assignment.getDateTimeAppointment());
            assignmentDTO.setDateTimeWhenToDo(assignment.getDateTimeWhenToDo());
            assignmentDTO.setAssignmentDescription(assignment.getAssignmentDescription());
            assignmentDTO.setIdUser(String.valueOf(assignment.getIdUser()));
            assignmentDTO.setClientId(String.valueOf(assignment.getClient().getId()));

            Client client = assignment.getClient();
            String clientName = client.getFirstName() + " " + client.getSurName();
            assignmentDTO.setClientName(clientName);

            assignmentDTO.setDoctorId(String.valueOf(assignment.getDoctor().getId()));
            assignmentDTO.setAssignmentType(assignment.getAssignmentType().name());
            assignmentDTO.setIsDone(assignment.getIsDone());
        } catch (Exception e) {
            LOGGER.error(marker, "An error has occurred in the 'mapAssignment_toAssignmentDTO' method ==> " + e);
            return null;
        }
        return assignmentDTO;
    }

    public List<String> getAssignmentType() {
        List<String> assignmentTypes = new ArrayList<>();
        Assignment.AssignmentType[] assignmentTypesArray = Assignment.AssignmentType.values();
        List<Assignment.AssignmentType> listOfAssignmentType = Arrays.asList(assignmentTypesArray);
        for (Assignment.AssignmentType aat : listOfAssignmentType) {
            String type = aat.name();
            assignmentTypes.add(type);
        }
        return assignmentTypes;
    }


    public List<AssignmentDTO> getAllAssignments() {
        List<Assignment> allAssignments = new ArrayList<>();
        List<AssignmentDTO> allAssignmentsDTO = new ArrayList<>();
        allAssignments = assignmentRepository.findAll();
        if (!allAssignments.isEmpty()) {
            for (Assignment assignment : allAssignments) {
                AssignmentDTO assignmentDTO = mapAssignment_toAssignmentDTO(assignment);
                allAssignmentsDTO.add(assignmentDTO);
            }
        }
        return allAssignmentsDTO;
    }


    public Assignment redactAssignment(String assignmentID, String assignmentCheckupAddress, String assignmentCheckupMobile,
                                       String assignmentCheckupEmail, String assignmentCheckupDescription, String assignmentDateTimeWhenToDo,
                                       String assignmentDescription, String assignmentClientId, String assignmentDoctorId,
                                       String assignmentType, String assignmentIsDone) {

        Marker marker = getLogMarker();
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(Long.valueOf(assignmentID));
        Assignment assignment = null;

        try {
            if (optionalAssignment.isPresent()) {
                assignment = optionalAssignment.get();

                if (assignmentCheckupAddress != null) {
                    assignment.setCheckupAddress(assignmentCheckupAddress);
                }
                if (assignmentCheckupMobile != null) {
                    assignment.setCheckupMobile(assignmentCheckupMobile);
                }
                if (assignmentCheckupEmail != null) {
                    assignment.setCheckupEmail(assignmentCheckupEmail);
                }
                if (assignmentCheckupDescription != null) {
                    assignment.setCheckupDescription(assignmentCheckupDescription);
                }
                if (assignmentDateTimeWhenToDo != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dateTime = LocalDateTime.parse(assignmentDateTimeWhenToDo, formatter);
                    assignment.setDateTimeWhenToDo(dateTime);
                }
                if (assignmentDescription != null) {
                    assignment.setAssignmentDescription(assignmentDescription);
                }
                if (assignmentClientId != null) {
                    assignment.setClient(clientRepository.getReferenceById(assignmentClientId));
                }
                if (assignmentDoctorId != null) {
                    assignment.setDoctor(doctorRepository.getReferenceById(Integer.valueOf(assignmentDoctorId)));
                }
                if (assignmentType != null) {
                    try {
                        Assignment.AssignmentType typeOfAssignment = Assignment.AssignmentType.valueOf(assignmentType);
                        assignment.setAssignmentType(typeOfAssignment);
                    } catch (IllegalArgumentException iae) {
                        LOGGER.error(marker, "No enum constant com.example.medicalservice.dto.Assignment.AssignmentType." + assignmentType);
                        LOGGER.error(marker, "Error received ==> " + iae);
                    }
                }
                if (assignmentIsDone != null) {
                    assignment.setIsDone(Boolean.valueOf(assignmentIsDone));
                }
                assignmentRepository.save(assignment);
                LOGGER.info(marker, " The object 'Assignment' has been successfully edited: " + assignment.toString());
            } else {
                LOGGER.error(marker, "Attempt to edit a assignment with ID = '" + assignmentID +
                        "' failed because the assignment with this ID does not exist in the database");
            }
        } catch (Exception e) {
            LOGGER.error(marker, "Error received ==> " + e);
            return null;
        }
        return assignment;
    }


    public List<AssignmentDTO> getAssignmentsForDay(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        LocalDateTime dataPlusDay = dateTime.plusDays(1);

        List<Assignment> assignmentsForDay = new ArrayList<>();
        List<AssignmentDTO> assignmentsDTOForDay = new ArrayList<>();
        assignmentsForDay = assignmentRepository.findByDateTimeWhenToDoBetween(dateTime, dataPlusDay);
        if (!assignmentsForDay.isEmpty()) {
            for (Assignment assignment : assignmentsForDay) {
                AssignmentDTO assignmentDTO = mapAssignment_toAssignmentDTO(assignment);
                assignmentsDTOForDay.add(assignmentDTO);
            }
        }
        return assignmentsDTOForDay;
    }


    public List<AssignmentDTO> getUnfulfilledAssignments() {


        LocalDateTime dateTimeToday = LocalDateTime.now();
        List<Assignment> unfulfilledAssignments = new ArrayList<>();
        List<AssignmentDTO> unfulfilledAssignmentsDTO = new ArrayList<>();
        unfulfilledAssignments = assignmentRepository.findByDateTimeWhenToDoIsBeforeAndIsDone(dateTimeToday, false);
        if (!unfulfilledAssignments.isEmpty()) {
            for (Assignment assignment : unfulfilledAssignments) {
                AssignmentDTO assignmentDTO = mapAssignment_toAssignmentDTO(assignment);
                unfulfilledAssignmentsDTO.add(assignmentDTO);
            }
        }
        return unfulfilledAssignmentsDTO;
    }


    public StatusDocAnswer getStatusClientDocuments(String id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            String clientPassportNumber = clientOptional.get().getPassportNumber();
            Optional<ClientDocs> clientDocsOpt = clientDocsRepository.findByPassportNumber(clientPassportNumber);
            ClientDocs clientDocs = clientDocsOpt.get();

            StatusDocAnswer statusDocAnswer = new StatusDocAnswer();
            if(clientDocs.getContractSignature() != null && clientDocs.getContractSignature() == true){
                statusDocAnswer.setContract(true);
            }
            if(clientDocs.getAgreementSignature() != null && clientDocs.getAgreementSignature() == true){
                statusDocAnswer.setAgreement(true);
            }
            if(clientDocs.getQuestionnaire() != null){
                statusDocAnswer.setQuestionnaire(true);
            }
            if(clientDocs.getClientFoto() != null){
                statusDocAnswer.setFoto(true);
            }
            return statusDocAnswer;
        }
        else {
            return null;
        }
    }

    //====================   pdf processing - second option   =======================================>
    public byte[] getCustomizedContract(String id) {

        Client client = clientRepository.getReferenceById(id);
        String firstName = client.getFirstName();
        String lastName = client.getLastName();
        String surname = client.getSurName();
        String insertName = firstName + " " + lastName + " " + surname;
        String insertPassportNumber = client.getPassportNumber();
        try {
            PDDocument pDDocument = PDDocument.load(new File("templateContract.pdf"));
            PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
            PDField field = pDAcroForm.getField("name");
            field.setValue(insertName);
            field = pDAcroForm.getField("passportNumber");
            field.setValue(insertPassportNumber);
            field = pDAcroForm.getField("address");
            field.setValue(client.getAddress());
            field = pDAcroForm.getField("phone");
            field.setValue(client.getTelephone());
            field = pDAcroForm.getField("email");
            field.setValue(client.getEmail());
            pDDocument.save("templateContractClient.pdf");
            pDDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when creating 'templateContractClient.pdf' for the client: " + client.getId());
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfReader reader = null;
        try {
            reader = new PdfReader("templateContractClient.pdf");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when reading the file: 'templateContractClient.pdf'");
        }
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        Document document = new Document(pdfDocument);
        document.close();
        byte[] pdfDocToByte = byteArrayOutputStream.toByteArray();

        // TODO   write an array of bytes to the database and erase the file
        ClientDocs clientDocs = null;
        Long idClientDocs = client.getClientDocs().getId();
        Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
        if (clientDocsOptional.isPresent()) {
            clientDocs = clientDocsOptional.get();
        }
        clientDocs.setContract(pdfDocToByte);
        try {
            client.setClientDocs(clientDocs);
            clientRepository.save(client);
            FileUtils.deleteQuietly(new File("templateContractClient.pdf"));
        }
        catch (Exception e){
            LOGGER.error("Error when saving an unsigned 'templateContractClient.pdf' to DB for the client: " + client.getId());
        }
        return pdfDocToByte;
    }


    public byte[] getCustomizedAgreement(String id) {
        Client client = clientRepository.getReferenceById(id);
        String firstName = client.getFirstName();
        String lastName = client.getLastName();
        String surname = client.getSurName();
        String insertName = firstName + " " + lastName + " " + surname;
        String insertPassportNumber = client.getPassportNumber();
        try {
            PDDocument pDDocument = PDDocument.load(new File("templateAgreement.pdf"));
            PDAcroForm pDAcroForm = pDDocument.getDocumentCatalog().getAcroForm();
            PDField field = pDAcroForm.getField("name");
            field.setValue(insertName);
            field = pDAcroForm.getField("passportNumber");
            field.setValue(insertPassportNumber);
            field = pDAcroForm.getField("address");
            field.setValue(client.getAddress());
            pDDocument.save("templateAgreementClient.pdf");
            pDDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when creating 'templateAgreementClient.pdf' for the client: " + client.getId());
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfReader reader = null;
        try {
            reader = new PdfReader("templateAgreementClient.pdf");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when reading the file: 'templateAgreementClient.pdf'");
        }
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        Document document = new Document(pdfDocument);
        document.close();
        byte[] pdfDocToByte = byteArrayOutputStream.toByteArray();

        ClientDocs clientDocs = null;
        Long idClientDocs = client.getClientDocs().getId();
        Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
        if (clientDocsOptional.isPresent()) {
            clientDocs = clientDocsOptional.get();
        }
        clientDocs.setAgreement(pdfDocToByte);
        try {
            client.setClientDocs(clientDocs);
            clientRepository.save(client);
            FileUtils.deleteQuietly(new File("templateAgreementClient.pdf"));
        }
        catch (Exception e){
            LOGGER.error("Error when saving an unsigned 'templateAgreementClient.pdf' to DB for the client: " + client.getId());
        }
        return pdfDocToByte;
    }


    public Boolean updateClientDocs(String clientID, String docType, MultipartFile signature) {
        String pdfContractFile = null;
        String pdfAgreementFile = null;
        byte[] signatureByte = null;
        String restoreSignature = null;
        String signedContract = "signedContract.pdf";
        String signedAgreement = "signedAgreement.pdf";
        Client client = null;
        ClientDocs clientDocs = null;


        try {
            signatureByte = signature.getBytes();
            restoreSignature = "restoreSignature.png";
            OutputStream out = new FileOutputStream(restoreSignature);
            out.write(signatureByte);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when reading 'MultipartFile' for the client: " + clientID);
            return false;
        }

  //==== resize signature ====== https://www.delftstack.com/ru/howto/java/java-resize-image/ =============>
        File fileToRead = new File(restoreSignature);
        BufferedImage bufferedImageInput = null;
//        int resizeWidth = 120;
//        int resizeHeight = 64;
        int resizeWidth = 108;
        int resizeHeight = 58;

        String imagePathToWrite = "resizeSignature.png";
        try {
            bufferedImageInput = ImageIO.read(fileToRead);
            BufferedImage bufferedImageOutput = new BufferedImage(resizeWidth, resizeHeight, bufferedImageInput.getType());

            Graphics2D g2d = bufferedImageOutput.createGraphics();
            g2d.drawImage(bufferedImageInput, 0, 0, resizeWidth, resizeHeight, null);
            g2d.dispose();

            String formatName = imagePathToWrite.substring(imagePathToWrite
                    .lastIndexOf(".") + 1);

            ImageIO.write(bufferedImageOutput, formatName, new File(imagePathToWrite));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error when changing client signature size: " + clientID);
            return false;
        }
  // ==================================================================================<

        if(docType.equals("Contract")){
            try {
                pdfContractFile = restoreContract_fromDB(clientID);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error reading 'Contract' from 'ClientDocs' for client: " + clientID);
                return false;
            }
            try {
                if(pdfContractFile != null) {
                    PDDocument doc = PDDocument.load(new File(pdfContractFile));
                    PDPage page = doc.getPage(5);
                    PDImageXObject pdImage = PDImageXObject.createFromFile(imagePathToWrite,doc);
                    PDPageContentStream contents = new PDPageContentStream(doc, page, true, true);
                    contents.drawImage(pdImage, 350, 550);
                    contents.close();
                    doc.save(signedContract);
                    doc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error when inserting the client's signature into the contract: " + clientID);
                return false;
            }

//=======================================================================================================>
// TODO   write an array of bytes to the database and erase the file
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfReader reader = null;
            try {
                reader = new PdfReader(signedContract);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error when reading the file: 'templateContractClient.pdf'");
                return false;
            }
            PdfDocument pdfDocument = new PdfDocument(reader, writer);
            Document document = new Document(pdfDocument);
            document.close();
            byte[] pdfDocToByte = byteArrayOutputStream.toByteArray();
            Optional<Client> clientOpt = clientRepository.findById(clientID);
            if(clientOpt.isPresent()) {
                client = clientOpt.get();
                Long idClientDocs = client.getClientDocs().getId();
                Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
                if (clientDocsOptional.isPresent()) {
                    clientDocs = clientDocsOptional.get();
                }
                clientDocs.setContract(pdfDocToByte);
                clientDocs.setContractSignature(true);
                try {
                    client.setClientDocs(clientDocs);
                    clientRepository.save(client);
                    FileUtils.deleteQuietly(new File(restoreSignature));
                    FileUtils.deleteQuietly(new File(imagePathToWrite));
                    FileUtils.deleteQuietly(new File(signedContract));
                    FileUtils.deleteQuietly(new File(pdfContractFile));
                } catch (Exception e) {
                    LOGGER.error("Error when saving an unsigned 'templateContractClient.pdf' to DB for the client: " + client.getId());
                    return false;
                }
            }
//=======================================================================================================<
        }

        if(docType.equals("Agreement")){
            try {
                pdfAgreementFile = restoreAgreement_fromDB(clientID);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error reading 'Agreement' from 'ClientDocs' for client: " + clientID);
                return false;
            }

            try {
                if(pdfAgreementFile != null) {
                    PDDocument doc = PDDocument.load(new File(pdfAgreementFile));
                    PDPage page = doc.getPage(0);
                    PDImageXObject pdImage = PDImageXObject.createFromFile(imagePathToWrite,doc);
                    PDPageContentStream contents = new PDPageContentStream(doc, page, true, true);
                    contents.drawImage(pdImage, 150, 275);
                    contents.close();
                    doc.save(signedAgreement);
                    doc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error when inserting the client's signature into the contract: " + clientID);
                return false;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfReader reader = null;
            try {
                reader = new PdfReader(signedAgreement);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error when reading the file: 'templateAgreementClient.pdf'");
                return false;
            }
            PdfDocument pdfDocument = new PdfDocument(reader, writer);
            Document document = new Document(pdfDocument);
            document.close();
            byte[] pdfDocToByte = byteArrayOutputStream.toByteArray();
            Optional<Client> clientOpt = clientRepository.findById(clientID);
            if(clientOpt.isPresent()) {
                client = clientOpt.get();
                Long idClientDocs = client.getClientDocs().getId();
                Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
                if (clientDocsOptional.isPresent()) {
                    clientDocs = clientDocsOptional.get();
                }
                clientDocs.setAgreement(pdfDocToByte);
                clientDocs.setAgreementSignature(true);
                try {
                    client.setClientDocs(clientDocs);
                    clientRepository.save(client);
                    FileUtils.deleteQuietly(new File(restoreSignature));
                    FileUtils.deleteQuietly(new File(imagePathToWrite));
                    FileUtils.deleteQuietly(new File(signedAgreement));
                    FileUtils.deleteQuietly(new File(pdfAgreementFile));
                } catch (Exception e) {
                    LOGGER.error("Error when saving an unsigned 'templateAgreementClient.pdf' to DB for the client: " + client.getId());
                    return false;
                }
            }
//=======================================================================================================<
        }
        return true;
    }

    private String restoreAgreement_fromDB(String clientID) throws IOException {
        Client client = null;
        ClientDocs clientDocs = null;
        String restoredAgreementFile = null;
        Optional<Client> clientOpt = clientRepository.findById(clientID);
        if(clientOpt.isPresent()) {
            client = clientOpt.get();
            Long idClientDocs = client.getClientDocs().getId();
            Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
            if (clientDocsOptional.isPresent()) {
                clientDocs = clientDocsOptional.get();
                byte[] agreementByte = clientDocs.getAgreement();
                restoredAgreementFile = "templateClientAgreement.pdf";
                OutputStream out = new FileOutputStream(restoredAgreementFile);
                out.write(agreementByte);
                out.close();
            }
          }
              return restoredAgreementFile;
    }


    public String restoreContract_fromDB(String clientID) throws IOException {
        Client client = null;
        ClientDocs clientDocs = null;
        String restoredContractFile = null;
        Optional<Client> clientOpt = clientRepository.findById(clientID);
        if(clientOpt.isPresent()){
            client = clientOpt.get();
            Long idClientDocs = client.getClientDocs().getId();
            Optional<ClientDocs> clientDocsOptional = clientDocsRepository.findById(idClientDocs);
            if (clientDocsOptional.isPresent()) {
                clientDocs = clientDocsOptional.get();
                byte[] contractByte = clientDocs.getContract();
                restoredContractFile = "templateClientContract.pdf";
                OutputStream out = new FileOutputStream(restoredContractFile);
                out.write(contractByte);
                out.close();
        }
      }
        return restoredContractFile;
    }


    public Boolean setClientFoto(String id, MultipartFile foto) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()){
            try {
                Client client = clientOptional.get();
                ClientDocs clientDocsCurrentClient = client.getClientDocs();
                clientDocsCurrentClient.setClientFoto(foto.getBytes());
                client.setClientDocs(clientDocsCurrentClient);
                clientRepository.save(client);
            }
            catch (Exception e){
                LOGGER.error("Error when saving the foto for the client: " + id);
                return false;
            }
        }
        else {
            LOGGER.error("Client with id= '" + id + "' does not exist");
            return false;
        }
        return true;
    }


    public byte[] getContractForDisplay(String id) {
        Client client = null;
        ClientDocs clientDocs = null;
        byte[] contractInPDF = null;
        Optional<Client> clientOpt = clientRepository.findById(id);
        if(clientOpt.isPresent()) {
            client = clientOpt.get();
            contractInPDF = client.getClientDocs().getContract();
        }
        return contractInPDF;
    }

    public byte[] getAgreementForDisplay(String id) {
        Client client = null;
        ClientDocs clientDocs = null;
        byte[] agreementInPDF = null;
        Optional<Client> clientOpt = clientRepository.findById(id);
        if(clientOpt.isPresent()) {
            client = clientOpt.get();
            agreementInPDF = client.getClientDocs().getAgreement();
        }
        return agreementInPDF;
    }





}

