package de.xwic.cube.webui.viewer;


public class SectionLineNavigationProvider extends TotalNavigationProvider {
	public SectionLineNavigationProvider() {
		
		setDataProvider(new AbstractCubeDataProvider(99) {
			public String getCellData(CubeViewerModel model, ContentInfo row,ContentInfo col) {
				return "";
			}
		});
	}

	@Override
	public NavigationProviderTypes getNavigationProviderType() {
		return NavigationProviderTypes.SECTION;
	}
}
