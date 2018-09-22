# gaussian-workbench

AUTHOR:
Josephine Meyer (jcmeyer@stanford.edu)
Stanford University

ABOUT:
Gaussian Workbench is a Java applet that simulates the propagation of Gaussian laser beams through systems of lenses. The user may add, remove, or adjust lenses at will using the buttons at the bottom of the screen; the beam profile will be updated accordingly. The applet also calculates the 1/e^2 radius and the radius of curvature of the beam at any point along the axis of the beam, and can locate the waist of the beam via the "Find Waist" feature. Optics arrangements can be saved for future use, making Gaussian Workbench ideal for prototyping.

Gaussian Workbench is also compatible with focus-tunable lenses, such as the Optotune EL-10-30 series. With a tunable lens selected, the user may sweep the focal length across its range using the "tune lens" slider bar, and the beam profile will be adjusted accordingly. (Some users have found that they prefer making all the lenses tunable for maximum control, even those that would be fixed in an actual experiment.)

Because Gaussian Workbench is a Java applet, it should work out of the box on most operating systems. (Just download and run GaussianWorkbench.jar; this should work as long as Java is installed.) In some cases, users have reported problems with the text and graphics appearing too small; this appears to be a problem with Java rather than the applet itself, as it appears to have gone away with a recent Java update.

HISTORY:
I developed the first iteration of Gaussian Workbench in August 2017 as an independent side project while working to develop a movable optical dipole trap using focus-tunable lenses under the guidance of Dr. Monika Schleier-Smith. Initially intended to simplify routine calculations for my own research, it quickly became apparent that the applet would be of use to physicists far beyond the Schleier-Smith lab. I received permission to release Gaussian Workbench as an open-source applet in September 2018.

LICENSE INFORMATION AND DISCLAIMER:
Gaussian Workbench relies on the Stanford ACM Java libraries for windows and graphics. The license information for the ACM libraries can be found here: https://www-cs-faculty.stanford.edu/people/eroberts/jtf/. All derivative works shall credit Stanford University and ACM (Association for Computing Machinery) for the use of the libraries, and in no instance shall any user claim ownership over the ACM libraries. Because the ACM libraries are not guaranteed to be backwards compatible, I have included the version of ACM.jar that was used in the creation of Gaussian Workbench.

Gaussian Workbench is intended for research and teaching applications only. Neither Gaussian Workbench nor derivative works may be sold or monetized, and all derivative works shall be made available as open-source software for research purposes.

Derivative works shall credit Josephine Meyer, Monika Schleier-Smith, and Stanford University. In no case may any other person or institution claim rights or copyright over Gaussian Workbench, nor distribute derivative works without proper attribution.

I (Josephine Meyer) reserve to restrict access to Gaussian Workbench or remove the code from Github entirely if it becomes clear that Gaussian Workbench is being abused.

While Gaussian Workbench has been rigorously tested, neither the author nor Stanford University accepts responsibility or liability for errors or omissions in the software, nor for damages resulting from the use or misuse of Gaussian Workbench. Verifying the accuracy of calculations obtained from Gaussian Workbench is solely the responsibility of the end user.

QUESTIONS OR COMMENTS:
If you have questions about how to operate Gaussian Workbench or believe you have identified a bug, please contact Josephine Meyer at jcmeyer@stanford.edu. (Note: if you are having trouble adding lenses, make sure you are not adding two lenses at identical or near-identical positions! This seems to be the most common problem reported by users.)

Time permitting, I am hoping to release Gaussian Workbench 2.0 in January 2019. Features I am considering include fine-tuning of lens position, off-axis capabilities, and dielectric media. If you have any additional suggestions for useful features, please contact me at jcmeyer@stanford.edu. (Please be advised that features will be added at my discretion. In particular, all features added should be compatible with the ABCD matrix formalism and should not significantly increase computing overhead.) Please also reach out to me if you would like to commit changes to Gaussian Workbench, particularly bug fixes and new features.


