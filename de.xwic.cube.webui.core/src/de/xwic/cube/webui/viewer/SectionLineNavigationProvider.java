package de.xwic.cube.webui.viewer;


public class SectionLineNavigationProvider extends TotalNavigationProvider {
	public SectionLineNavigationProvider() {
		this("");
	}
	
	public SectionLineNavigationProvider(String title) {
		setTitle(title);
		setDataProvider(new AbstractCubeDataProvider(99) {
			@Override
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
