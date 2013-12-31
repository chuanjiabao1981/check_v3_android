package com.check.v3.utils;

import java.io.File;
import java.net.URI;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Utility class for <code>Uri</code>
 * 
 * @see android.net.Uri
 */
public final class UriUtils {

    private static final String URI_ENCODE_SKIP_CHARS = File.pathSeparator + File.separator;

    private UriUtils() {
        // Hidden
    }

    /**
     * Checks if given Uri is encoded. <br/>
     * NB. This method can return true for the file name 'a%25.txt' or similar. There is no way to detect if it's
     * encoded of not for such names.
     * 
     * @param uri
     *            Uri as string
     * @return TRUE if uri is encoded; FALSE otherwise
     * 
     * @see #isContentUri(Uri)
     */
    public static boolean isUriEncoded(String uri) {
        String decoded = Uri.decode(uri);
        String encoded = Uri.encode(decoded, URI_ENCODE_SKIP_CHARS);
        return uri.equals(encoded);
    }

    /**
     * @see #isUriEncoded(String)
     */
    public static boolean isUriEncoded(Uri uri) {
        return isUriEncoded(uri.toString());
    }

    /**
     * Returns new decoded Uri if it's encoded. Can return the same reference if not encoded.
     * 
     * @param uri
     *            Uri to decode
     * @return decoded Uri
     * @see #isUriEncoded(Uri)
     * 
     * @throws NullPointerException
     *             if uri is null
     */
    public static Uri getDecodedUri(Uri uri) {
        Uri result = uri;
        if (isUriEncoded(result)) {
            result = Uri.parse(Uri.decode(result.toString()));
        }
        return result;
    }

    /**
     * @see #getDecodedUri(Uri)
     */
    public static Uri getDecodedUri(String uri) {
        return getDecodedUri(Uri.parse(uri));
    }

    /**
     * Returns new encoded Uri if not encoded yet. Can return the same reference if already encoded.
     * 
     * @param uri
     *            Uri to decode
     * @return decoded Uri
     * @see #isUriEncoded(Uri)
     * 
     * @throws NullPointerException
     *             if uri is null
     */
    public static Uri getEncodedUri(Uri uri) {
        Uri result = uri;
        if (!isUriEncoded(result)) {
            result = Uri.parse(Uri.encode(result.toString(), URI_ENCODE_SKIP_CHARS));
        }
        return result;
    }

    /**
     * @see #getEncodedUri(Uri)
     */
    public static Uri getEncodedUri(String uri) {
        String result = uri;
        if (!isUriEncoded(result)) {
            result = Uri.encode(result, URI_ENCODE_SKIP_CHARS);
        }
        return Uri.parse(result);
    }

    /**
     * Creates File from Uri.
     * 
     * @param uri
     *            Uri
     * @return File
     * 
     * @throws {@link IllegalArgumentException} if not a file Uri
     * @throws NullPointerException
     *             if uri is null
     */
    public static File fileFromUri(Uri uri) {
        return fileFromUri(uri.toString());
    }

    /**
     * @see #fileFromUri(Uri)
     */
    public static File fileFromUri(String uri) {
        if (!isFileUri(uri)) {
            throw new IllegalArgumentException("not a file uri: " + uri);
        }

        if (isUriEncoded(uri)) {
            return new File(URI.create(uri));
        }
        return new File(URI.create(getEncodedUri(uri).toString()));
    }

    public static final boolean isFileUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme());
    }

    public static final boolean isContentUri(Uri uri) {
        return ContentResolver.SCHEME_CONTENT.equals(uri.getScheme());
    }

    public static final boolean isResourceUri(Uri uri) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme());
    }

    public static final boolean isFileUri(String uri) {
        return isFileUri(Uri.parse(uri));
    }

    public static final boolean isContentUri(String uri) {
        return isContentUri(Uri.parse(uri));
    }

    public static final boolean isResourceUri(String uri) {
        return isResourceUri(Uri.parse(uri));
    }
}

