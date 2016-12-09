package eu.openminted.store;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;

import eu.openminted.store.config.Storage;
import eu.openminted.store.idgenerator.IdGenerator;
import eu.openminted.store.index.StoreIndex;

/**
 * An {@link eu.openminted.store.StoreService} implementation for local disk.
 * @author galanisd
 *
 */
public class StoreServiceLocalDisk extends StoreServiceGeneric{
		
	/**
	 * Constructor.
	 * @param storageProperties
	 * @param idGen
	 * @param storageIndex
	 */
	@Autowired
	public StoreServiceLocalDisk(StorePropertiesLocal storageProperties, IdGenerator idGen, StoreIndex storageIndex){		
		super(Storage.LOCAL, storageProperties, idGen, storageIndex);		 
	}		
	
}
