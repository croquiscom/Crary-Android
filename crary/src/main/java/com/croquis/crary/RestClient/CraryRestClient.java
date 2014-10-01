package com.croquis.crary.RestClient;

import android.content.Context;

import com.croquis.crary.OnTaskComplete;

public class CraryRestClient {
	public static class RestError extends Throwable {
		private static final long serialVersionUID = 1L;
		public String error;
		public String description;

		public RestError(String error, String description) {
			this.error = error;
			this.description = description;
		}

		public static final RestError NETWORK_ERROR = new RestError("network error", "");
		public static final RestError UNRECOGNIZABLE_RESULT = new RestError("unrecognizable result", "");
		public static final RestError UNKNOWN_ERROR = new RestError("unknown error", "");
	}

	public interface OnRequestComplete<T> extends OnTaskComplete<RestError, T> {
	}

	private static CraryRestClient sSharedClient;

	private static CraryRestClient sharedClient(Context context) {
		if (sSharedClient == null) {
			sSharedClient = new CraryRestClient(context);
		}

		return sSharedClient;
	}

	private CraryRestClient(Context context) {

	}
}
