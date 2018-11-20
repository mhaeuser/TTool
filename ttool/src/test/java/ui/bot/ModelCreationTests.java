/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file gather all the tests on the creation of one and single model.
 * All the tests check the creation of the model, it development to it suppression 
 */

package ui.bot;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

/*
 * Class ModelCreationTests
 * Creation: 19/11/2018
 * @version 1.0 19/11/2018
 * @author Arthur VUAGNIAUX
*/

public class ModelCreationTests extends AssertJSwingJUnitTestCase {
	private FrameFixture window;
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main(false, false, false, false, false, false, false, false, false, false, false, false, false));
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}
