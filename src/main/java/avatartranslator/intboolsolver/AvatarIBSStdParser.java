/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package avatartranslator.intboolsolver;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.*;

/**
 * class IBSStdClosedFormulaSolver is a complete implementation
 * and instantiation of {@link IBSOriginParser
 * IBSolver} for closed Formulas.
 *
 * <p> It is provided for documentation together with
 * {@link IBSStdClosedFormulaAttributeClass
 * IBSStdClosedFormulaAttributeClass} and
 * {@link IBSStdClosedFormulaAttributeClass
 * IBSStdClosedFormulaAttribute}}</p>
 *
 * <p>These three
 * classes provides the same features as
 * {@link IBSClosedFormulaAttributeClass
 * IBSClosedFormulaAttribute},
 * {@link IBSClosedFormulaAttributeClass
 * IBSClosedFormulaAttributeClass} and
 * {@link IBSClosedFormulaParser
 * IBSClosedFormulaSolver} (together).</p>
 *
 * Creation: 07/03/2023
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert
 */

public class AvatarIBSStdParser extends IBSStdParser<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock> {
        public AvatarIBSStdParser() {
                super();
                setAttributeClass(new AvatarIBSAttributeClass());
                setExpressionClass(new AvatarIBSExpressionClass());
        }
        public AvatarIBSStdParser(
                IBSAttributeClass<
                        AvatarSpecification,
                        AvatarBlock,
                        AvatarStateMachineElement,
                        SpecificationState,
                        SpecificationBlock> _c,
                IBSExpressionClass<
                        AvatarSpecification,
                        AvatarBlock,
                        AvatarStateMachineElement,
                        SpecificationState,
                        SpecificationBlock> _e) {
                super();
                setAttributeClass(_c);
                setExpressionClass(_e);
        }
}