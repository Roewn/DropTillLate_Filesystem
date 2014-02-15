/**
 * 
 */
package ch.droptilllate.filesystem.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileException;
import ch.droptilllate.filesystem.io.IFile;
import ch.droptilllate.filesystem.io.IShareRelation;
import ch.droptilllate.filesystem.io.ShareRelationHandler;
import ch.droptilllate.filesystem.truezip.FileHandler;

/**
 * @author Roewn
 * 
 */
public class WorkerListFiles implements  Callable<List<FileInfo>>
{
	private IShareRelation iShareRelation = new ShareRelationHandler();
	private String shareRelation;
	private String key;
	private List<FileInfo> fiResultList;

	public WorkerListFiles(String shareRelation, String key)
	{
		this.shareRelation = shareRelation;
		this.key = key;
		this.fiResultList = new ArrayList<FileInfo>();
	}

	
	@Override
	public List<FileInfo> call() throws Exception
	{
		System.out.println("Thread started: " + Thread.currentThread().getName() + " -> Relation: " + shareRelation);
		fiResultList = iShareRelation.getFilesOfShareRelation(shareRelation, key);
		
		fiResultList.add(new FileInfo(444));
		return fiResultList;		
	}

}
