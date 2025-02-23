package model;

import java.util.ArrayList;
import java.util.List;

import org.openntf.xsp.jakarta.nosql.mapping.extension.DominoRepository;
import org.openntf.xsp.jakarta.nosql.mapping.extension.RepositoryProvider;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class GameDownloadPlan {
	@RepositoryProvider("storage")
	public interface Repository extends DominoRepository<GameDownloadPlan, String> {
		
	}
	
	public enum State {
		Planned, InProgress, Complete, Exception
	}
	
	@Id
	private String documentId;
	@Column
	private int gameId;
	@Column
	private State state = State.Planned;
	@Column
	private String stackTrace;
	@Column
	private String gameDocumentId;
	@Column
	private List<String> installerIds;
	@Column
	private List<String> extraIds;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	public String getGameDocumentId() {
		return gameDocumentId;
	}
	public void setGameDocumentId(String gameDocumentId) {
		this.gameDocumentId = gameDocumentId;
	}
	
	public List<String> getExtraIds() {
		return extraIds;
	}
	public void setExtraIds(List<String> extraIds) {
		this.extraIds = extraIds;
	}
	public void addExtra(GameExtra gameExtra) {
		List<String> extraIds = this.extraIds;
		if(extraIds == null) {
			extraIds = new ArrayList<>();
		}
		extraIds.add(gameExtra.documentId());
		this.extraIds = extraIds;
	}
	
	public List<String> getInstallerIds() {
		return installerIds;
	}
	public void setInstallerIds(List<String> installerIds) {
		this.installerIds = installerIds;
	}
	public void addInstaller(Installer installer) {
		List<String> installerIds = this.installerIds;
		if(installerIds == null) {
			installerIds = new ArrayList<>();
		}
		installerIds.add(installer.documentId());
		this.installerIds = installerIds;
	}
	
}
