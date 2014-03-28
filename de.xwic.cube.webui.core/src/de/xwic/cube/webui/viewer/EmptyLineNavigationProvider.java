package de.xwic.cube.webui.viewer;


public class EmptyLineNavigationProvider extends TotalNavigationProvider {
	public EmptyLineNavigationProvider() {
		setTitle("");
		setDataProvider(new AbstractCubeDataProvider(99) {
			public String getCellData(CubeViewerModel model, ContentInfo row,ContentInfo col) {
				return "";
			}
		});
	}
	

	@Override
	public NavigationProviderTypes getNavigationProviderType() {
		return NavigationProviderTypes.EMPTY;
	}
}
