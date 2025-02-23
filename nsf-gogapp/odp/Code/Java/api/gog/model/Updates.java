package api.gog.model;

public record Updates(
	int messages,
	int pendingFriendRequests,
	int unreadChatMessages,
	int products,
	int forum,
	int total
) {}
