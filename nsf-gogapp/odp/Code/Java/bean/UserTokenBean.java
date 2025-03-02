package bean;

import java.util.List;

import org.openntf.xsp.jakarta.nosql.mapping.extension.ViewQuery;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.UserToken;

@Dependent
@Named("userTokens")
public class UserTokenBean {
	@Inject
	private UserToken.Repository userTokens;
	
	public List<UserToken> getAll() {
		return userTokens.list().toList();
	}
	
	public boolean isAnyExist() {
		return userTokens.list().findFirst().map(t -> true).orElse(false);
	}
	
	public List<UserToken> forType(String type) {
		return userTokens.findByType(ViewQuery.query().key(type, true)).toList();
	}
}
