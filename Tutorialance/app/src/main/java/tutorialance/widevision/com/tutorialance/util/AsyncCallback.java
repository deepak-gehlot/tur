package tutorialance.widevision.com.tutorialance.util;

public interface AsyncCallback<T>
{
	public void onOperationCompleted(T result, Exception e);
}
