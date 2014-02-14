package ch.droptilllate.filesystem.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.filesystem.api.FileHandlingSummary;
import ch.droptilllate.filesystem.api.FileSystemHandlerConcurrent;
import ch.droptilllate.filesystem.api.IFileSystem;
import ch.droptilllate.filesystem.commons.Constants;
import ch.droptilllate.filesystem.info.ContainerInfo;
import ch.droptilllate.filesystem.info.FileInfo;
import ch.droptilllate.filesystem.info.FileInfoDecrypt;
import ch.droptilllate.filesystem.info.FileInfoEncrypt;
import ch.droptilllate.filesystem.info.InfoHelper;
import ch.droptilllate.filesystem.io.FileOperator;

public class TempTest
{
	public static void main(String[] args) {

		String testPath = "C:\\01_Development\\IndividualProject\\testfiles\\";
		String extractPath = testPath + "extract\\";
		
		FileHandlingSummary fhs = null;


		// Prepare File Infos
		String plainFileName5 = "5MB.zip";
		String plainFileName10 = "10MB.zip";
		String plainFileName20 = "20MB.zip";
		String plainFileName50 = "50MB.zip";
		String plainFileName100 = "100MB.zip";
		String plainFileName200 = "200MB.zip";
		String plainFileName512 = "512MB.zip";
		String plainFileName1024 = "1024MB.zip";
		
		String plainFileNameTmp = "test2.docx";
		
		
		List<String> fileNameList = new ArrayList<String>();
		fileNameList.add(plainFileName5);
		fileNameList.add(plainFileName10);
		fileNameList.add(plainFileName20);
//		fileNameList.add(plainFileName50);
//		fileNameList.add(plainFileName100);
//		fileNameList.add(plainFileName200);
//		fileNameList.add(plainFileName512);
//		fileNameList.add(plainFileName1024);
		
//		fileNameList.add(plainFileNameTmp);
				

		// Wait for user Permission
		boolean waitForUser = false;
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		if (waitForUser)
		{
			try
			{
				System.out.println("To start test press Enter");
				input.readLine();
			} catch (IOException e)
			{
			}
		}
		
		// Get the fileSystemHandler
		// IFileSystem fileSystemHandler = new FileSystemHandler();
		IFileSystem fileSystemHandler = new FileSystemHandlerConcurrent();

		// Start Test
		// System.out.println("-------------------------------------------------");
		// System.out.println("Operation;Size [MB]; Time [ms]");
		{

			// Test cases
			switch (1)
			{
			case 1: // Encrypt Files 
				ArrayList<FileInfoEncrypt> fileInfoEncList = new ArrayList<FileInfoEncrypt>();
				int ie = 0;
				for (String name : fileNameList) {					
					fileInfoEncList.add(new FileInfoEncrypt(ie, testPath + name, testPath));
					ie++;
				}					
				fhs = fileSystemHandler.encryptFiles(fileInfoEncList);
				FileOperator.listFileAssignment(fileInfoEncList);
				break;
				
			case 2:	 // Decrypt Files			
				ArrayList<FileInfoDecrypt> fileInfoDecList = new ArrayList<FileInfoDecrypt>();
				int id = 0;
				int[] containerIds = {03231321, 45454, 464564};
				for (String name : fileNameList) {
//					fileInfoDecList.add(new FileInfoDecrypt(id,InfoHelper.checkFileExt(name), extractPath,testPath, containerIds[id]));
					fileInfoDecList.add(new FileInfoDecrypt(id,InfoHelper.checkFileExt(name), extractPath,testPath, 347290));
					id++;
				}
				fhs = fileSystemHandler.decryptFiles(fileInfoDecList);
				break;
				
			case 3: // Delete Files		
				ArrayList<FileInfo> fileInfoDelList = new ArrayList<FileInfo>();
				
				fileInfoDelList.add(new FileInfo(0,new ContainerInfo(114094, testPath)));
					
				fhs = fileSystemHandler.deleteFiles(fileInfoDelList);
				
				System.out.println(FileOperator.isFileInContainer(fileInfoDelList.get(0)));
				FileOperator.listFileAssignment(fileInfoDelList);
				break;
				
				
			default:

			}
		}

		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println(fhs);

		// Wait for user Permission
		if (waitForUser)
		{
			try
			{
				System.out.println("To stop the test press Enter");
				input.readLine();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	

}
