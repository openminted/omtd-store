package eu.openminted.store.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import eu.openminted.store.common.StoreREST;
import eu.openminted.store.common.StoreResponse;
import eu.openminted.store.core.StoreException;
import eu.openminted.store.core.StoreService;
import eu.openminted.utils.webservices.Utils;

/**
 * A Spring Controller for the Store Service.
 * @author galanisd
 *
 */
@Controller
public class StoreController {

	private static final Logger log = LoggerFactory.getLogger(StoreController.class);
	
    private final StoreService storeService;

    /**
     * Constructor.
     * @param storeService
     */
    @Autowired
    public StoreController(StoreService storeService) {
       this.storeService = storeService;
    }   
    
    /**
     * Creates an archive.
     * @return
     */
    @RequestMapping(value=StoreREST.createArchive, method=RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public StoreResponse createArchive(){
    	String response = String.valueOf(storeService.createArchive()); 
    	return new StoreResponse(response, "");    
    }

    /**
     * Creates an archive with the provided id.
     * @param archiveId
     * @return
     */
    @RequestMapping(value=StoreREST.createArchive, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse createArchive(@RequestParam(StoreREST.archiveID) String archiveId){
    	String response = String.valueOf(storeService.createArchive(archiveId)); 
    	return new StoreResponse(response, "");        	
    }

    /**
     * Checks if the given archive exists.
     * @param archiveId
     * @return
     */
    @RequestMapping(value=StoreREST.archiveExists, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse archiveExists(@RequestParam(StoreREST.archiveID) String archiveId){
    	String response = String.valueOf(storeService.archiveExists(archiveId));
    	return new StoreResponse(response, "");
    }

    /**
     * Check if the given file exists in an archive. 
     * @param archiveId
     * @param fileName
     * @return
     */
    @RequestMapping(value=StoreREST.fileExistsInArchive, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse fileExistsInArchive(@RequestParam(StoreREST.archiveID)String archiveId, @RequestParam(StoreREST.fileName) String fileName){
    	String response = String.valueOf(storeService.fileExistsInArchive(archiveId, fileName));
    	return new StoreResponse(response, "");
    }
    
    /**
     * Creates a subArchive under a given archive.
     * @return
     */
    @RequestMapping(value=StoreREST.createSubArchive, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse createSubArchive(@RequestParam(StoreREST.archiveID) String archiveId, @RequestParam("archiveName") String archiveName){
    	String response = storeService.createArchive(archiveId, archiveName);
    	return new StoreResponse(response, "");
    }

    /**
     * Finalize archive
     * @return
     */
    @RequestMapping(value=StoreREST.finalizeArchive, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse finalizeArchive(@RequestParam(StoreREST.archiveID) String archiveId){
    	String response = String.valueOf(storeService.finalizeArchive(archiveId));
    	return new StoreResponse(response, "");
    }
       	
    /**
     * Download archive
     * @return
     */
    @RequestMapping(value=StoreREST.downloadArchive, method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Resource> downloadArchive(@RequestParam(StoreREST.archiveID) String archiveId){
        InputStream fileInputStream = storeService.downloadArchive(archiveId);
        return Utils.download(fileInputStream, archiveId);
    }
    
    /**
     * List files
     * @return a list of files.
     */
    @RequestMapping(value=StoreREST.listFiles, method=RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public StoreResponse listFiles(){
    	String response = storeService.listAllFiles();  
    	return new StoreResponse(response, "");    	   
    }

    /**
     * List files in Archive.
     * @param archiveID
     * @return a list of files.
     */
    @RequestMapping(value=StoreREST.listFilesInArch, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<String> listFilesInArch(@RequestParam(StoreREST.archiveID) String archiveID,
                                        @RequestParam(StoreREST.listDirectories) boolean listDirectories,
                                        @RequestParam(StoreREST.recursive) boolean recursive){
    	return storeService.listFiles(archiveID, listDirectories, recursive);
    }

    /**
     * List files in Archive.
     * @param archiveID
     * @param from
     * @param size
     * @return a list of files.
     */
    @RequestMapping(value=StoreREST.listFilesPaged, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<String> listFilesPaged(@RequestParam(StoreREST.archiveID) String archiveID,
                                       @RequestParam(StoreREST.fileListIndex) int from,
                                       @RequestParam(StoreREST.fileListSize) int size){
    	return storeService.listFiles(archiveID, from, size);
    }

//    /** // TODO: remove
//     * List Publications in Corpus.
//     * @param corpusID
//     * @param from
//     * @param size
//     * @return a list of publications.
//     */
//    @RequestMapping(value=StoreREST.listFilesPaged, method=RequestMethod.POST, produces = "application/json")
//    @ResponseBody
//    public StoreResponse listCorpus(@RequestParam(value = "corpusID") String corpusID, @RequestParam(value = "from") int from, @RequestParam(value = "size") int size){
//    	String response = storeService.listCorpus(corpusID, from, size);
//    	return new StoreResponse(response, "");
//    }
    
    /**
     * Delete all files.
     * @return action status
     */
    @RequestMapping(value=StoreREST.deleteAll, method=RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public StoreResponse deleteAll(){
    	String response = String.valueOf(storeService.deleteAll());
    	return new StoreResponse(response, "");  
    }
    
    /**
     * Delete archive.
     * @return action status
     */
    @RequestMapping(value=StoreREST.deleteArchive, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse deleteArchive(@RequestParam(StoreREST.archiveID) String archiveId){
    	String response = String.valueOf(storeService.deleteArchive(archiveId, true));
    	return new StoreResponse(response, "");
    }    

    /**
     * Delete file.
     * @return action status
     */
    @RequestMapping(value=StoreREST.deleteFile, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse deleteFile(@RequestParam(StoreREST.archiveID) String archiveId, @RequestParam(StoreREST.fileName) String fileName){
    	String response = String.valueOf(storeService.deleteFile(archiveId, fileName));
    	return new StoreResponse(response, "");
    }
    
    /**
     * Upload file.
     * @param archiveId
     * @param fileName
     * @param file
     * @return action status.
     */
    @RequestMapping(value=StoreREST.uploadFile, method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public StoreResponse uploadFile(@RequestParam(StoreREST.archiveID) String archiveId, @RequestParam(StoreREST.fileName) String fileName, @RequestParam(StoreREST.file) MultipartFile file) {
    	StoreResponse resp = new StoreResponse();
    	String response = ""; 
    	
    	try{
    		response = String.valueOf(storeService.storeFile(archiveId, file.getInputStream(), fileName));
    		resp.setResponse(response);
    	}catch(Exception e){
    		log.debug("ERROR", e);
    		resp.setResponse(String.valueOf(Boolean.FALSE));
    		resp.setReport(e.getMessage());
    	}
    	
    	return resp;
    }
    
    /** 
     * Download file
     * @param fname
     * @return
     */
    @RequestMapping(value=StoreREST.downloadFile, method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam(StoreREST.fileName) String fileName){
       	    	
        InputStream fileInputStream = storeService.downloadFile(fileName);
        return Utils.download(fileInputStream, fileName);
	}
    
}
