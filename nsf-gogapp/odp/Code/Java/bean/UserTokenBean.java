package bean;

import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.UserToken;

@Dependent
@Named("userTokens")
public class UserTokenBean {
	@Inject
	private UserToken.Repository userTokens;
	
	public Optional<UserToken> getActive() {
		return userTokens.findAll().findFirst();
	}
}
