package com.spire.web;

public enum WebMessage
{
	OK(200, "OK"),
	GONE(410, "Gone"),
	FOUND(302, "Found"),
	LOCKED(423, "Locked"),
	CREATED(201, "Created"),
	ACCEPTED(202, "Accepted"),
	CONFLICT(409, "Conflict"),
	SEE_OTHER(303, "See Other"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	NO_CONTENT(204, "No Content"),
	BAD_GATEWAY(502, "Bad Gateway"),
	BAD_REQUEST(400, "Bad Request"),
	NOT_EXTENDED(510, "No Extended"),
	MULTI_STATUS(207, "Multi-Status"),
	UNAUTHORIZED(401, "Unauthorized"),
	NOT_MODIFIED(304, "Not Modified"),
	REDIRECT(301, "Moved Permanently"),
	LOOP_DETECTED(508, "Loop Detected"),
	RESET_CONTENT(205, "Reset Content"),
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	LENGTH_REQUIRED(411, "Length Required"),
	NOT_IMPLEMENTED(501, "Not Implemented"),
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	REQUEST_TIMEOUT(408, "Request Timeout"),
	PARTIAL_CONTENT(206, "Partial Content"),
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	ALREADY_REPORTED(208, "Already Reported"),
	PAYMENT_REQUIRED(402, "Payment Required"),
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	SERVER_ERROR(500, "Internal Server Error"),
	EXPECTION_FAILED(417, "Expectation Failed"),
	TOO_MANY_REQUESTS(429, "Too Many Requests"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	PERMANENT_REDIRECT(308, "Permanent Redirect"),
	PRECONDITION_FAILED(412, "Precondition Failed"),
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	NSUFFICIENT_STORAGE(507, "Insufficient Storage"),
	REQUESTED_URI_TOO_LONG(414, "Request-URI Too Long"),
	PRECONDITION_REQUIRED(428, "Precondition Required"),
	AUTH_REQUIRED(511, "Network Authentication Required"),
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
	HTTP_VER_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
	NON_AUTHORIZED_INFO(203, "Non-Authoritative Information"),
	PROXY_AUTH_REQUIRED(407, "Proxy Authentication Required"),
	REQUEST_HEAD_TOO_LARGE(431, "Request Header Fields Too Large"),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable");
	
	public static final WebMessage getMessage(int StatusID)
	{
		WebMessage[] a = values();
		for(int b = 0; b < a.length; b++) if(a[b].messageID == StatusID) return a[b];
		return null;
	}
	
	protected short messageID;
	protected String messageText;
	
	private WebMessage(int MessageID, String Message)
	{
		messageText = Message;
		messageID = (short)MessageID;
	}
}