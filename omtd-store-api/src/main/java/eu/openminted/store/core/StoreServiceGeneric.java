package eu.openminted.store.core;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import eu.openminted.store.config.StoreProperties;
import eu.openminted.store.fsconnector.FSConnector;
import eu.openminted.store.fsconnector.FSConnectorBuilder;
import eu.openminted.store.idgenerator.IdGenerator;
import eu.openminted.store.metadata.StoreMetadata;

/**
 * 
 * @author galanisd
 *
 */
public class StoreServiceGeneric implements StoreService{
		
	private static final Logger log = LoggerFactory.getLogger(StoreServiceGeneric.class);
	
	private String type;
	protected StoreProperties storeProperties;
	protected IdGenerator idGen;
	protected StoreMetadata storeMetadata;
	private FSConnector connector;
	
	/**
	 * Constructor.
	 * @param type
	 * @param storeProperties
	 * @param idGen
	 * @param storeMetadata
	 */
	public StoreServiceGeneric(String type, StoreProperties storeProperties, IdGenerator idGen, StoreMetadata storeMetadata) {
		super();
		connector = FSConnectorBuilder.getConnector(type, storeProperties);				
		this.type = type;
		this.storeProperties = storeProperties;
		this.idGen = idGen;
		this.storeMetadata = storeMetadata;
		
	}
	
	public IdGenerator getIdGen() {
		return idGen;
	}
	
	public void setIdGen(IdGenerator idGen) {
		this.idGen = idGen;
	}
	
	public StoreProperties getStoreProperties() {
		return storeProperties;
	}

	public void setStoreProperties(StoreProperties storageProperties) {
		this.storeProperties = storageProperties;
	}

	public StoreMetadata getStoreMetadata() {
		return storeMetadata;
	}

	public void setStoreIndex(StoreMetadata storageIndex) {
		this.storeMetadata = storageIndex;
	}
	
	@Override
	public boolean createArchive(String archiveId) {
		// Create Folder.

		String destinationFolderAbsolutePath = Helper.getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		log.info(destinationFolderAbsolutePath.toString());
		boolean creationStatus = connector.makeFolder(destinationFolderAbsolutePath);
		
		return creationStatus; 
	}
	
	@Override
	public String createArchive() {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		
		// Get new archive id.
		String archiveId = idGen.generateId();
		boolean creationStatus = createArchive(archiveId);		
		// Return archiveId.
    	if(creationStatus){    		
    		return archiveId;
    	}else{
    		return null;
    	}
	}

	@Override
	public String createArchive(String parentArchiveId, String name) {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		
		// Get new archive id.
		String archiveId = name;
		// Create Folder.
		String destinationFolderAbsolutePathForParent = Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), parentArchiveId);
		String destinationFolderAbsolutePath = Helper.appendToPath(destinationFolderAbsolutePathForParent, archiveId);
		log.info(destinationFolderAbsolutePath.toString());
		boolean creationStatus = connector.makeFolder(destinationFolderAbsolutePath);
		// Return archiveId.
    	if(creationStatus){
    		//storageIndex.addChildArchive(parentArchiveId, archiveId);
    		return parentArchiveId + "/" + archiveId;    		
    	}else{
    		return null;
    	}
	}

	@Override
	public String cloneArchive(String archiveId) {

	    if (!archiveExists(archiveId)) {
	        log.info("cloneArchive : source archive does not exist");
	        return null;
        }

	    // Create new archive id.
		String newArchiveId;
		do {
			newArchiveId = idGen.generateId();
		} while (archiveExists(newArchiveId));
		boolean creationStatus = createArchive(newArchiveId);

		if(creationStatus){
            String srcAbsolutePath = Helper.
                    getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
            String dstAbsolutePath = Helper.
                    getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), newArchiveId);
            log.info("cloneArchive : source: " + srcAbsolutePath);
            log.info("cloneArchive : destination: " + dstAbsolutePath);
			if (connector.copyContent(srcAbsolutePath, dstAbsolutePath)) {
				return newArchiveId;
			} else deleteArchive(newArchiveId, true);
		}
		return null;
	}


	@Override
	public boolean deleteArchive(String archiveId, boolean force) {
		String destinationFolderAbsolutePath = Helper.getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		// TODO: Also delete zip.
		return connector.deleteFolder(destinationFolderAbsolutePath, force);
		
	}

	@Override
	public InputStream downloadArchive(String archiveId) {
		String destinationFile = storeProperties.getStorageRoot() + archiveId + ".zip";
		return connector.download(destinationFile);
	}

	@Override
	public InputStream fetchMetadata(String archiveId) {
        finalizeArchive(archiveId + "/metadata"); // only used to compress the metadata directory
        InputStream inStream = downloadFile(archiveId + "/metadata.zip");
        deleteFile(archiveId, "metadata.zip"); // delete the metadata.zip file that was created
        return inStream;
	}

	@Override
	public InputStream fetchAnnotations(String archiveId) {
        finalizeArchive(archiveId + "/annotations"); // only used to compress the metadata directory
        InputStream inStream = downloadFile(archiveId + "/annotations.zip");
        deleteFile(archiveId, "annotations.zip"); // delete the metadata.zip file that was created
        return inStream;
	}

    @Override
    public long archiveSize(String archiveId) {
        long size = 0;

        Path folder = Paths.get(Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId));
        try {
            size = Files.walk(folder)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return size;
    }

	@Override
	public long archiveSizeOnDisk(String archiveId) {
        long size = 0;

        log.info("archiveSize : archiveId = " + archiveId);
        if (archiveExists(archiveId)) {
            File dir = new File(Helper.
                    getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId));
            size = FileUtils.sizeOfDirectory(dir);
            log.info("archiveSize : size = "+ Long.toString(size));
        }
	    return size;
	}

	@Override
	public ArchiveInfo getArchiveInfo(String archiveId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean storeFile(String archiveId, InputStream is, String fileName) {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		String destinationFolderAbsolutePathForParent = Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		String destinationFile = Helper.appendToPath(destinationFolderAbsolutePathForParent, fileName);

		return connector.storeFile(destinationFile, is);
		
	}

	@Override
	public boolean storeFile(InputStream is, String fileName) {		
		return storeFile(null, is, fileName) ;
	}
	
	@Override
	public boolean deleteFile(String archiveId, String fileName) {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		String destinationFolderAbsolutePathForParent = Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		String destinationFile = Helper.appendToPath(destinationFolderAbsolutePathForParent, fileName);
		return connector.deleteFile(destinationFile);
					
	}

	@Override
	public boolean moveFile(String archiveId, String fileName, String moveTo) {
		String sourceFolderAbsolutePathForParent = Helper.
				getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);

		String destinationFolderAbsolutePathForParent = Helper.
				appendToPath(sourceFolderAbsolutePathForParent, moveTo);

		sourceFolderAbsolutePathForParent = Helper.appendToPath(sourceFolderAbsolutePathForParent, fileName);

		log.info("Copying " + sourceFolderAbsolutePathForParent + " to " + destinationFolderAbsolutePathForParent);

		return connector.copyContent(sourceFolderAbsolutePathForParent, destinationFolderAbsolutePathForParent) && connector.deleteFile(sourceFolderAbsolutePathForParent);
	}

	@Override
	public boolean deleteAll() {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		return connector.deleteAll();
	}
	
	@Override
	public String listAllFiles() {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);		
		return connector.listAllFiles();
	}

	@Override
	public List<String> listFiles(String fileName, boolean listDirectories, boolean recursive, boolean ignoreZips) {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		return connector.listFiles(fileName, listDirectories, recursive, ignoreZips);
	}

	@Override
	public List<String> listFiles(String fileName, int from, int size) {
		//FSConnector connector = FSConnectorBuilder.getConnector(type, storageProperties);
		return connector.listFiles(fileName, from, size);
	}

	@Override
	public InputStream downloadFile(String fileName) {
		String destinationFile = storeProperties.getStorageRoot() + fileName;
		return connector.download(destinationFile);
	}

	@Override
	public boolean finalizeArchive(String archiveId) {
		String destinationZip = storeProperties.getStorageRoot() + archiveId + ".zip";
		return connector.compressDir(storeProperties.getStorageRoot() + archiveId + "/", destinationZip);
	}

	@Override
	public boolean archiveExists(String archiveId) {
		String destinationFolderAbsolutePathForParent = Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		return connector.isDir(destinationFolderAbsolutePathForParent);
	}

	@Override
	public boolean fileExistsInArchive(String archiveId, String fileName) {
		String destinationFolderAbsolutePathForParent = Helper.
                getAbsolutePathForArchive(storeMetadata, storeProperties.getStorageRoot(), archiveId);
		String destinationFile = Helper.appendToPath(destinationFolderAbsolutePathForParent, fileName);
		return connector.exists(destinationFile);
	}

}
