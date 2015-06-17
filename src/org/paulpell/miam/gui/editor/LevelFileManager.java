package org.paulpell.miam.gui.editor;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.paulpell.miam.logic.levels.Level;
import org.paulpell.miam.net.LevelEncoder;


public class LevelFileManager
{

	static final String DEFAULT_FOLDER = "saves";
	
	LevelEditor editor_;
	File currentFile_;
	
	public LevelFileManager(LevelEditor le)
	{
		editor_ = le;
	}
	
	
	protected void saveLevelToCurrentFile(Level l)
	{
		if (null == currentFile_)
			saveLevelToNewFile(l);
		else
			writeLevelToFile(l, currentFile_);
	}
	
	protected void saveLevelToNewFile(Level l)
	{
		FileDialog saveDialog = new FileDialog(editor_, "Save level", FileDialog.SAVE);
		saveDialog.setDirectory(DEFAULT_FOLDER);
		
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
				editor_.displayMessage("Could not create file : " + e.getMessage());
			}	
		}
		if (!f.canWrite())
		{
			editor_.displayMessage("Can not write to file: " + f.getAbsolutePath());
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
			editor_.displayMessage("Can not write level: " +e.getMessage());
		}
	}
	
	protected Level openLevel()
	{
		FileDialog openDialog = new FileDialog(editor_, "Choose level", FileDialog.LOAD);
		openDialog.setMultipleMode(false);
		openDialog.setDirectory(DEFAULT_FOLDER);
		
		openDialog.setVisible(true);
		
		File[] f = openDialog.getFiles();
		
		if (f.length != 0)
		{
			currentFile_ = f[0];
			try
			{
				return LevelFileManager.readLevelFromFile(currentFile_);
			}
			catch (IOException ioe)
			{
				return null;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		return null;
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
