package com.spring.form.web;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.spring.form.model.Attachment;
import com.spring.form.model.Donation;
import com.spring.form.model.Login;
import com.spring.form.model.User;
import com.spring.form.service.AttachmentService;
import com.spring.form.service.DonationService;
import com.spring.form.service.UserService;
import com.spring.form.validator.DonationFormValidator;
import com.spring.form.validator.UserFormValidator;

//http://www.tikalk.com/redirectattributes-new-feature-spring-mvc-31/
//https://en.wikipedia.org/wiki/Post/Redirect/Get
//http://www.oschina.net/translate/spring-mvc-flash-attribute-example
@Controller
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	private String role = "";

	@Autowired
	UserFormValidator userFormValidator;

	@Autowired
	DonationFormValidator donationFormValidator;

	@InitBinder("userForm")
	protected void initUserBinder(WebDataBinder binder) {
		binder.addValidators(userFormValidator);
	}

	@InitBinder("donationForm")
	protected void initDonationBinder(WebDataBinder binder) {
		binder.addValidators(donationFormValidator);
	}

	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		// Convert multipart object to byte[]
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	private UserService userService;
	private DonationService donationService;
	private AttachmentService attachmentService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setDonationService(DonationService donationService) {
		this.donationService = donationService;
	}

	@Autowired
	public void setAttachmentService(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		logger.debug("index()");
		return "redirect:/main";

	}

	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main(Model model) {

		logger.debug("main()");

		Login login = new Login();

		model.addAttribute("loginForm", login);

		return "login/loginform";

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@ModelAttribute("loginForm") Login login, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		logger.debug("login()");

		String username = login.getUsername();
		String password = login.getPassword();
		User user = userService.findByLoginName(username);

		if (user == null || !user.getPassword().equals(password)) {
			redirectAttributes.addFlashAttribute("css", "danger");
			redirectAttributes.addFlashAttribute("msg", "The username or password is incorrect.");
			return "redirect:/main";
		} else {
			role = user.getRole();
			redirectAttributes.addFlashAttribute("css", "success");
			redirectAttributes.addFlashAttribute("msg", role + " Logged in successfully!");
			if (role.equals("Donor")) {
				redirectAttributes.addFlashAttribute("role", role);
				return "redirect:/donations/" + user.getId() + "/add";
			} else {
				return "redirect:/dashboard";
			}
		}

	}

	// log out
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, final RedirectAttributes redirectAttributes) {

		logger.debug("logout()");
		role = "";
		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "Logged out successfully!");
		return "redirect:/main";

	}
	
	// staff dashboard
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(Model model) {

		logger.debug("dashboard()");
		return "login/dashboard";

	}

	// list page
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String showAllUsers(Model model) {

		logger.debug("showAllUsers()");
		model.addAttribute("users", userService.findAll());
		return "users/list";

	}

	// save or update user
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public String saveOrUpdateUser(@ModelAttribute("userForm") @Validated User user, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		logger.debug("saveOrUpdateUser() : {}", user);

		if (result.hasErrors()) {
			populateDefaultUserModel(model);
			return "users/userform";
		} else {

			redirectAttributes.addFlashAttribute("css", "success");
			if (user.isNew()) {
				redirectAttributes.addFlashAttribute("msg", "User added successfully!");
			} else {
				redirectAttributes.addFlashAttribute("msg", "User updated successfully!");
			}

			userService.saveOrUpdate(user);

			// POST/REDIRECT/GET
			return "redirect:/users";

			// POST/FORWARD/GET
			// return "user/list";

		}

	}

	// show add user form
	@RequestMapping(value = "/users/add", method = RequestMethod.GET)
	public String showAddUserForm(Model model) {

		logger.debug("showAddUserForm()");

		User user = new User();

		model.addAttribute("userForm", user);

		populateDefaultUserModel(model);

		return "users/userform";

	}

	// show update form
	@RequestMapping(value = "/users/{id}/update", method = RequestMethod.GET)
	public String showUpdateUserForm(@PathVariable("id") int id, Model model) {

		logger.debug("showUpdateUserForm() : {}", id);

		User user = userService.findById(id);
		model.addAttribute("userForm", user);

		populateDefaultUserModel(model);

		return "users/userform";

	}

	// delete user
	@RequestMapping(value = "/users/{id}/delete", method = RequestMethod.GET)
	public String deleteUser(@PathVariable("id") int id, final RedirectAttributes redirectAttributes) {

		logger.debug("deleteUser() : {}", id);

		userService.delete(id);

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "User is deleted!");

		return "redirect:/users";

	}

	// show user
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public String showUser(@PathVariable("id") int id, Model model) {

		logger.debug("showUser() id: {}", id);

		User user = userService.findById(id);
		if (user == null) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "User not found");
		}
		model.addAttribute("user", user);

		return "users/show";

	}

	// donation list page
	@RequestMapping(value = "/donations", method = RequestMethod.GET)
	public String showAllDonations(Model model) {

		logger.debug("showAllDonations()");
		model.addAttribute("donations", donationService.findAll());
		return "donations/list";

	}

	// save or update donation
	@RequestMapping(value = "/donations", method = RequestMethod.POST)
	public String saveOrUpdateDonation(@ModelAttribute("donationForm") @Validated Donation donation,
			BindingResult result, Model model, final RedirectAttributes redirectAttributes,
			HttpServletResponse response, HttpServletRequest request) {

		logger.debug("saveOrUpdateDonation() : {}", donation);

		if (result.hasErrors()) {
			populateDefaultDonationModel(model);
			return "donations/donateform";
		} else {

			redirectAttributes.addFlashAttribute("css", "success");
			if (donation.isNew()) {
				donation.setNumImages(0);
				redirectAttributes.addFlashAttribute("msg", "Donation added successfully!");
			} else {
				redirectAttributes.addFlashAttribute("msg", "Donation updated successfully!");
			}

			donation.setTacking(new Timestamp(new java.util.Date().getTime()));
			donationService.saveOrUpdate(donation);

			int numImages;
			if (donation.getNumImages() == null) {
				numImages = 0;
			} else {
				numImages = donation.getNumImages();
			}

			if (numImages == 4) {
				redirectAttributes.addFlashAttribute("max", "Max number of images reached; images were not uploaded");
			} else {
				int id = donation.getId();
				try {
					InputStream input = donation.getFile1().getInputStream();
					ImageIO.read(input).toString();
					saveAttachment(donation.getFile1(), id);
					numImages++;
					redirectAttributes.addFlashAttribute("file1", "File 1: Image uploaded");
				} catch (Exception e) {
					redirectAttributes.addFlashAttribute("file1", "File 1: No image detected");
				}
				if (numImages == 4) {
					redirectAttributes.addFlashAttribute("max",
							"Max number of images reached; rest of images were not uploaded");
				} else {
					try {
						InputStream input = donation.getFile2().getInputStream();
						ImageIO.read(input).toString();
						saveAttachment(donation.getFile2(), id);
						numImages++;
						redirectAttributes.addFlashAttribute("file2", "File 2: Image uploaded");
					} catch (Exception e) {
						redirectAttributes.addFlashAttribute("file2", "File 2: No image detected");
					}
					if (numImages == 4) {
						redirectAttributes.addFlashAttribute("max",
								"Max number of images reached; rest of images were not uploaded");
					} else {
						try {
							InputStream input = donation.getFile3().getInputStream();
							ImageIO.read(input).toString();
							saveAttachment(donation.getFile3(), id);
							numImages++;
							redirectAttributes.addFlashAttribute("file3", "File 3: Image uploaded");
						} catch (Exception e) {
							redirectAttributes.addFlashAttribute("file3", "File 3: No image detected");
						}
						if (numImages == 4) {
							redirectAttributes.addFlashAttribute("max",
									"Max number of images reached; rest of images were not uploaded");
						} else {
							try {
								InputStream input = donation.getFile4().getInputStream();
								ImageIO.read(input).toString();
								saveAttachment(donation.getFile4(), id);
								numImages++;
								redirectAttributes.addFlashAttribute("file4", "File 4: Image uploaded");
							} catch (Exception e) {
								redirectAttributes.addFlashAttribute("file4", "File 4: No image detected");
							}
						}
					}
				}
			}

			donation.setNumImages(numImages);
			donationService.saveOrUpdate(donation);
			redirectAttributes.addFlashAttribute("role", role);

			// POST/REDIRECT/GET
			return "redirect:/confirmation";

			// POST/FORWARD/GET
			// return "confirmation/confirm";

		}

	}

	// show add donation form
	@RequestMapping(value = "/donations/{id}/add", method = RequestMethod.GET)
	public String showAddDonationForm(Model model, @PathVariable("id") int id) {

		logger.debug("showAddDonationForm()");

		Donation donation = new Donation();
		User donor = userService.findById(id);
		java.util.Date date = new java.util.Date();

		// set default value
		donation.setDonor(donor.getId());
		donation.setScheduledDate(new Date(date.getTime()));
		donation.setAddress(donor.getAddress());
		donation.setCity(donor.getCity());
		donation.setProvince(donor.getProvince());
		donation.setPostalCode(donor.getPostalCode());

		model.addAttribute("donationForm", donation);
		model.addAttribute("noImage", true);

		populateDefaultDonationModel(model);

		return "donations/donateform";

	}

	// show update form
	@RequestMapping(value = "/donations/{id}/update", method = RequestMethod.GET)
	public String showUpdateDonationForm(@PathVariable("id") int id, Model model) {

		logger.debug("showUpdateDonationForm() : {}", id);

		Donation donation = donationService.findById(id);
		List<Attachment> images = attachmentService.findByDonation(id);
		model.addAttribute("donationForm", donation);

		Boolean noImage = false;
		List<Integer> imageIds = new ArrayList<>();
		for (int ctr = 0; ctr < images.size(); ctr++) {
			Attachment attach = images.get(ctr);
			if (attach.getImage() != null) {
				imageIds.add(attach.getId());
			}
		}
		if (imageIds.size() == 0) {
			noImage = true;
		} else {
			model.addAttribute("imageIds", imageIds);
		}
		model.addAttribute("noImage", noImage);

		populateDefaultDonationModel(model);

		return "donations/donateform";

	}

	// delete donation
	@RequestMapping(value = "/donations/{id}/delete", method = RequestMethod.GET)
	public String deleteDonation(@PathVariable("id") int id, final RedirectAttributes redirectAttributes) {

		logger.debug("deleteDonation() : {}", id);

		List<Attachment> images = attachmentService.findByDonation(id);

		for (int ctr = 0; ctr < images.size(); ctr++) {
			attachmentService.delete(images.get(ctr).getId());
		}

		donationService.delete(id);

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "Donation is deleted!");

		return "redirect:/donations";

	}

	// show donation
	@RequestMapping(value = "/donations/{id}", method = RequestMethod.GET)
	public String showDonation(@PathVariable("id") int id, Model model, HttpServletResponse response,
			HttpServletRequest request) {

		logger.debug("showDonation() donation id: {}", id);

		Donation donation = donationService.findById(id);
		List<Attachment> images = attachmentService.findByDonation(id);

		if (donation == null) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "User not found");
		}
		model.addAttribute("donation", donation);

		Boolean noImage = false;
		List<Integer> imageIds = new ArrayList<>();
		for (int ctr = 0; ctr < images.size(); ctr++) {
			Attachment attach = images.get(ctr);
			if (attach.getImage() != null) {
				imageIds.add(attach.getId());
			}
		}
		if (imageIds.size() == 0) {
			noImage = true;
		} else {
			model.addAttribute("imageIds", imageIds);
		}
		model.addAttribute("noImage", noImage);

		return "donations/show";

	}

	// donation confirm page
	@RequestMapping(value = "/confirmation", method = RequestMethod.GET)
	public String donationConfirm(Model model) {

		logger.debug("donationConfirm()");
		return "confirmation/confirm";

	}

	// display image
	@RequestMapping(value = "/images/{id}", method = RequestMethod.GET)
	private void displayImages(@PathVariable("id") int id, Model model, HttpServletResponse response,
			HttpServletRequest request) {

		logger.debug("displayImages() image id: {}", id);

		try {
			Attachment attach = attachmentService.findById(id);
			byte[] image = attach.getBytes();
			if (image != null) {
				response.reset();
				response.setContentType("image/png");
				response.setContentLength(image.length);
				response.getOutputStream().write(image);
				response.getOutputStream().close();
			}
		} catch (IOException e) {
			logger.debug("displayImages() IO Exception : {}", e.getCause());
		}

	}

	// save attachment
	public void saveAttachment(MultipartFile file, int id) {

		logger.debug("saveAttachment() file : {}", file);

		Attachment attach = new Attachment();
		attach.setFile(file);
		attach.setDonation(id);
		attachmentService.saveOrUpdate(attach);

	}

	// delete attachment
	@RequestMapping(value = "/images/{id}/delete", method = RequestMethod.POST)
	public String deleteAttachment(@PathVariable("id") int id, final RedirectAttributes redirectAttributes) {

		logger.debug("deleteAttachment() : {}", id);

		int donId = attachmentService.findById(id).getDonation();
		attachmentService.delete(id);
		Donation donation = donationService.findById(donId);
		donation.decreaseNumImages(1);
		donationService.saveOrUpdate(donation);

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "Image is deleted!");

		return "redirect:/donations/" + donId + "/update";

	}

	private void populateDefaultUserModel(Model model) {

		populateProvinces(model);

	}

	private void populateDefaultDonationModel(Model model) {

		populateProvinces(model);

	}

	private void populateProvinces(Model model) {

		Map<String, String> province = new LinkedHashMap<String, String>();
		province.put("AB", "Alberta");
		province.put("BC", "British Columbia");
		province.put("MB", "Manitoba");
		province.put("NB", "New Brunswick");
		province.put("NL", "Newfoundland & Labrador");
		province.put("NS", "Nova Scotia");
		province.put("ON", "Ontario");
		province.put("PE", "Prince Edward Island");
		province.put("QE", "Quebec");
		province.put("SA", "Saskatchewan");
		province.put("NT", "Northwest Territories");
		province.put("NU", "Nunavut");
		province.put("YU", "Yukon");
		model.addAttribute("provinceList", province);

	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ModelAndView handleEmptyData(HttpServletRequest req, Exception ex) {

		logger.debug("handleEmptyData()");
		logger.error("Request: {}, error ", req.getRequestURL(), ex);

		ModelAndView model = new ModelAndView();
		model.setViewName("user/show");
		model.addObject("msg", "user not found");

		return model;

	}

	/*
	 * private byte[] convertImageToBytes(File source) {
	 * 
	 * logger.debug("convertImageToBytes() source name: {}", source.getName());
	 * byte[] bytes = null;
	 * 
	 * try { ByteArrayOutputStream baos = new ByteArrayOutputStream(); BufferedImage
	 * image = ImageIO.read(source); ImageIO.write(image, "png", baos);
	 * baos.flush(); bytes = baos.toByteArray(); baos.close(); } catch (IOException
	 * e) { logger.debug("convertImageToBytes() IO Exception : {}", e.getCause()); }
	 * 
	 * return bytes;
	 * 
	 * }
	 */

}