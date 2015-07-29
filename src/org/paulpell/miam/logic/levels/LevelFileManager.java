package org.paulpell.miam.logic.levels;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.paulpell.miam.logic.Constants;
import org.paulpell.miam.net.LevelEncoder;


public class LevelFileManager
{

	
	LevelEditorControl leControl_;
	File currentFile_;
	
	public LevelFileManager(LevelEditorControl lec)
	{
		leControl_ = lec;
	}
	

	public static String[] listLevels()
	{
		ArrayList <String> levelNames = new ArrayList <String> ();
		levelNames.add(Constants.DEFAULT_LEVEL_NAME);
		
		try
		{
			File folder = new File(Constants.LEVEL_FOLDER);
			if (folder != null)
			{
				for (String s : folder.list())
					levelNames.add(s);
			}
		}
		catch (Exception e)
		{}

		String[] s = new String[]{};
		s = levelNames.toArray(s);
		return s;
	}
	
	public static Level readLevelFromDefaultDir(String name)
			throws Exception
	{
		File f = new File(
				"." + File.separatorChar
				+ Constants.LEVEL_FOLDER + File.separatorChar
				+ name);
		return readLevelFromFile(f);
	}
	
	
	protected void saveLevelToCurrentFile(Level l, JFrame parent)
	{
		if (null == currentFile_)
			saveLevelToNewFile(l, parent);
		else
			writeLevelToFile(l, currentFile_);
	}
	
	protected void saveLevelToNewFile(Level l, JFrame parent)
	{
		FileDialog saveDialog = new FileDialog(parent, "Save level", FileDialog.SAVE);
		saveDialog.setDirectory(Constants.LEVEL_FOLDER);

		saveDialog.toFront();
		saveDialog.setVisible(true);
		
		File[] f = saveDialog.getFiles();
		if (f.length != 0)
		{
			currentFile_ = f[0];
			writeLevelToFile(l, currentFile_);
		}
	}
	
	private void writeLevelToFile(Level l, File f)
	{
		if (!f.exists())
		{
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				leControl_.displayMessage("Could not create file : " + e.getMessage());
			}	
		}
		if (!f.canWrite())
		{
			leControl_.displayMessage("Can not write to file: " + f.getAbsolutePath());
			return;
		}
		
		try
		{
			byte[] bytes = LevelEncoder.encodeLevel(l);
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(bytes);
			fout.close();
			
		}
		catch (Exception e)
		{
			leControl_.displayMessage("Can not write level: " +e.getMessage());
		}
	}
	
	protected Level openLevel(JFrame parent)
			throws Exception
	{
		FileDialog openDialog = new FileDialog(parent, "Choose level", FileDialog.LOAD);
		openDialog.setMultipleMode(false);
		openDialog.setDirectory(Constants.LEVEL_FOLDER);
		
		openDialog.toFront();
		
		openDialog.setVisible(true);
		
		File[] f = openDialog.getFiles();
		
		if (f.length == 0)
			return null;
		
		currentFile_ = f[0];
		return LevelFileManager.readLevelFromFile(currentFile_);
	}

	public static Level readLevelFromFile(File f)
			throws Exception
	{
		int len;
		if (f.length() > Integer.MAX_VALUE)
			throw new Exception("Cannot handle too big files!");
		
		len = (int)f.length();
		byte[] bytes = new byte[len];
		FileInputStream fin = new FileInputStream(f);
		int n = fin.read(bytes, 0, len);
		fin.close();
		if (n != len)
			throw new Exception("Could not read the file");
		
		return LevelEncoder.decodeLevel(bytes);
	}

}
