package com.dms.app.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.image.BufferedImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dms.app.config.AddBarcodeToPDF;
import com.dms.app.config.BarCodeImage;
import com.dms.app.config.CombineIntoPDF;
import com.dms.app.config.CreateFolder;
import com.dms.app.config.DateTimeUtil;
import com.dms.app.config.ImageUtils;
import com.dms.app.model.ActiveLinkMenu;
import com.dms.app.model.Category;
import com.dms.app.model.Department;
import com.dms.app.model.Document;
import com.dms.app.model.DocumentData;
import com.dms.app.model.DocumentRevised;
import com.dms.app.model.DocumentSubType;
import com.dms.app.model.DocumentType;
import com.dms.app.model.DocumentView;
import com.dms.app.model.DocumentViewReport;
import com.dms.app.model.Project;
import com.dms.app.model.User;
import com.dms.app.repository.CategoryRepository;
import com.dms.app.repository.DepartmentRepository;
import com.dms.app.repository.DocumentRepository;
import com.dms.app.repository.DocumentRevisedRepository;
import com.dms.app.repository.DocumentSubTypeRepository;
import com.dms.app.repository.DocumentTypeRepository;
import com.dms.app.repository.DocumentViewRepository;
import com.dms.app.repository.ProjectRepository;
import com.dms.app.repository.UserRepository;
import com.dms.app.service.S3StorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@CrossOrigin
public class HomeController {

	 public HomeController() {
	
	 }
	
	 @Value("${app.storage.temp-dir:/tmp/dms-storage/}")
	 private String tempStorageRoot;

	 @Value("${app.context-url:}")
	 private String CONTEXT_URL;
	 
	 private final String EMPLOYEE_DEFAULT_IMG	  = "user.png";
	
	// private final String SERVER_URL			  = "http://app.ysminfosolution.com/img/";
	// private final String UPLOAD_DIR       	  = "/home/rdpuser/Documents/docs/";
	// private final String CONTEXT_URL			  = "/dms";
	 
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	
	@Autowired
	private DocumentSubTypeRepository documentSubTypeRepository;
	
	@Autowired
	private DocumentRevisedRepository documentRevisedRepository;
	
	@Autowired
	private DocumentViewRepository documentViewRepository;

	@Autowired
	private S3StorageService s3StorageService;
	
	Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@GetMapping("/")
	public String stsexam(Model model) {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}

		return "login";
	}
	
	@GetMapping("/dashboard")
	public String dashboard2(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		menu.setDashboardLink("active");			
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		model.addAttribute("projectList", projectRepository.findAllByOrderByProjectIdDesc());
		return "dashboard2";
	}	

	@GetMapping("/add-user")
	public String addUser(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	
		model.addAttribute("departmentList", departmentRepository.findAll());
		menu.setUserLink("active");
		menu.setAddUserLink("active");
		menu.setUserMenuOpen("menu-open");
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		
		return "add-user";
	}
	
	@PostMapping("/save-user")
    public String saveUser(@ModelAttribute ActiveLinkMenu menu, @RequestParam("userPhoto") MultipartFile userPhoto, @ModelAttribute("user") User user, 
    		BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) {
				
		String pwd = user.getPassword();
		
		String fileName1 = EMPLOYEE_DEFAULT_IMG;
		   
	     if(userPhoto.getSize() != 0 && user.getId() == 0) {
	    	     String passport  = getSafeFileName(userPhoto.getOriginalFilename());
			     long   timestmp  = new Timestamp(System.currentTimeMillis()).getTime();
			     String extension = getFileExtension(passport);
	  		     
			      fileName1 = "dp"+timestmp+extension;
			      s3StorageService.uploadFile(fileName1, userPhoto);
	     }
		
		try {
			String encryptPwd = passwordEncoder.encode(pwd);
			user.setPassword(encryptPwd);		
			user.setEnabled(true);
			user.setUserPasswordRead(pwd);
			user.setUserPhoto(fileName1);
			String userName = principal.getName();
			User usr = userRepository.getUserByUserName(userName);
			user.setUserAddedBy(usr.getId());
			user.setCreatedDatetime(DateTimeUtil.getSysDateTime());
			userRepository.save(user);
			
			redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
			return "redirect:/view-users";
		} catch (Exception e) {
			// TODO: handle exception
			
			redirectAttrs.addFlashAttribute("messageError", "Mobile number already registered.");
			return "redirect:/add-user";
		}		
	}
	
	@PostMapping("/update-user")
    public String updateUser(@ModelAttribute ActiveLinkMenu menu,@ModelAttribute("user") User user, 
    		BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) {
		
		String pwd = user.getPassword();
		try {
			String encryptPwd = passwordEncoder.encode(pwd);
			user.setPassword(encryptPwd);
			user.setUserPasswordRead(pwd);			
			userRepository.save(user);
			
			redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
			return "redirect:/view-users";
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("update-user : " + e.toString());
			redirectAttrs.addFlashAttribute("messageError", "Mobile number already registered.");
			return "redirect:/add-user";
		}		
	}
	
	@GetMapping("/view-users")
	public String viewUsers(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		
		List<User> usersList = userRepository.findAll();
		usersList.forEach(p -> {			
			   Department d = departmentRepository.findById(p.getDepartmentId()).get();			 
			   p.setDepartmentName(d.getDepartmentName());			   
		});
		
		model.addAttribute("usersList", usersList);
		menu.setUserLink("active");
		menu.setViewUserLink("active");
		menu.setUserMenuOpen("menu-open");
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		
		return "view-users";
	}
	
	@GetMapping("/edit-user/{id}") 
    public String editUser(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int id) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			
			User usr = userRepository.getById(id);
			
			model.addAttribute("departmentList", departmentRepository.findAll());
			model.addAttribute("user", user);
			model.addAttribute("usr", usr);
			menu.setUserLink("active");
			menu.setAddUserLink("active");
			menu.setUserMenuOpen("menu-open");
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
			
			return "edit-user";
	 }
		 
	 @GetMapping("/delete-user/{id}/{enabled}") 
     public String editUser(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, 
    		@PathVariable int id, @PathVariable boolean enabled, RedirectAttributes redirectAttrs) {
		 		 
			userRepository.updateUserStatus(enabled, id);
			if(enabled == false)
			   redirectAttrs.addFlashAttribute("messageError", "Account Deactivated.");
			else
			   redirectAttrs.addFlashAttribute("messageSuccess", "Account Activated.");
			
			return "redirect:/view-users";
	 }
	 
	 
	@GetMapping("/add-department")
	public String addDepartment(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		
		menu.setDepartmentLink("active");
		menu.setAddDepartmentLink("active");
		menu.setDepartmentMenuOpen("menu-open");
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		
		return "add-department";
	}
	
	@PostMapping("/save-department")
    public String saveDepartment(@ModelAttribute ActiveLinkMenu menu, @ModelAttribute("department") Department department, 
    		 BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) {
				
		     Department ctgr = departmentRepository.getDepartmentByDepartmentName(department.getDepartmentName());
		
		     if(ctgr == null) {
				 String userName = principal.getName();
				 User user = userRepository.getUserByUserName(userName);
			     int userId = user.getId();
			
			     department.setUserId(userId);
			     department.setCreatedDatetime(DateTimeUtil.getSysDateTime());
			     
			     departmentRepository.save(department);
			     redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
		     } else {
		    	 redirectAttrs.addFlashAttribute("messageSuccess", "Department already exist.");
		     }
						 
			 return "redirect:/view-department";			
	}
	
	@GetMapping("/edit-department/{departmentId}") 
    public String editDepartment(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int departmentId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			
			Department department = departmentRepository.getById(departmentId);
			
			model.addAttribute("user", user);
			model.addAttribute("department", department);
			
			menu.setDepartmentLink("active");
			menu.setAddDepartmentLink("active");
			menu.setDepartmentMenuOpen("menu-open");
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
			
			return "edit-department";
	}
	
	@GetMapping("/view-department")
	public String viewDepartment(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		
		model.addAttribute("departmentList", departmentRepository.findAll());
		menu.setDepartmentLink("active");
		menu.setViewDepartmentLink("active");
		menu.setDepartmentMenuOpen("menu-open");
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		
		return "view-department";
	}	
	
	
	/*
	@PostMapping("/uploadDashboardProfilePicture")
	public @ResponseBody void uploadLogo(@RequestParam("file") MultipartFile logo, @RequestParam("employeeId") int employeeId) {
					
		try {
			
			String passport  = logo.getOriginalFilename();
			String fileName1 = "dp"+DateTimeUtil.getTimeStampInMiliseconds()+passport.substring(passport.lastIndexOf("."));

			File file = new File(UPLOAD_DIR + fileName1);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(logo.getBytes());
				fos.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			employeeRepository.uploadProfilePicture(employeeId, fileName1);

		} catch (Exception e) {
			logger.error("Exceptin while uploadDashboardProfilePicture : " + e.toString());
		}
	}
	*/
	
	@PostMapping("/uploadUserProfilePicture")
	public @ResponseBody void uploadUserlogo(@RequestParam("file") MultipartFile logo, @RequestParam("userId") int userId) {
					
		try {
			//System.out.println("userId : "+userId);
			
			String passport  = getSafeFileName(logo.getOriginalFilename());
			String fileName1 = "dp"+DateTimeUtil.getTimeStampInMiliseconds()+getFileExtension(passport);

			s3StorageService.uploadFile(fileName1, logo);
			
			userRepository.uploadProfilePicture(userId, fileName1);
			
		} catch (Exception e) {
			logger.error("Exception while uploadUserProfilePicture : " + e.toString());
		}
	}
	
		
	@GetMapping("/add-project")
	public String addProject(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		menu.setDashboardLink("active");			
		model.addAttribute("menu", menu);
		model.addAttribute("filePath", getServerUrl());
		model.addAttribute("contextPath", CONTEXT_URL);
		
		return "add-project";
	}
	
	@PostMapping("/save-project")
    public String saveProject(@ModelAttribute ActiveLinkMenu menu, @ModelAttribute("project") Project project, 
    		BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) {
			
	
		try {
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			
			project.setUserId(user.getId());
			project.setCreatedDatetime(DateTimeUtil.getSysDateTime());
			projectRepository.save(project);
			
			redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
			return "redirect:/dashboard";
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			redirectAttrs.addFlashAttribute("messageError", "Something went wrong. Please try again.");
			return "redirect:/dashboard";
		}		
	}
	
	 @GetMapping("/documents/{projectId}") 
     public String documents(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int projectId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
									
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
			model.addAttribute("project", projectRepository.getById(projectId));
			model.addAttribute("projectId", projectId);
			
			List<Document> documentList = documentRepository.getDocumentByProjectId(projectId);
			
			documentList.forEach(doc -> {
				
				Category category = categoryRepository.getById(doc.getDocumentCategory());
				int departmentId = category.getDepartmentId();			
				String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
				
				doc.setDepartmentName(departmentName);
				doc.setCategoryName(category.getCategoryName());
				doc.setDocumentTypeName(documentTypeRepository.getById(doc.getDocumentType()).getDocumentTypeName());
				if(doc.getDocumentSubType() != 0) {
					doc.setDocumentSubTypeName(documentSubTypeRepository.getById(doc.getDocumentSubType()).getDocumentSubTypeName());
				} else {
					doc.setDocumentSubTypeName("-");
				}
			  	 
			});
			
			model.addAttribute("documents", documentList);
			
			return "view-documents";
	 }
	 
	 @GetMapping("/timeline/{projectId}")
	 public String timeline(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int projectId) {
			
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
			model.addAttribute("project", projectRepository.getById(projectId));
			model.addAttribute("projectId", projectId);
			
			List<DocumentRevised> documentRevisedList = documentRevisedRepository.getRevisedDocumentByProjectId(projectId);
			documentRevisedList.forEach(doc -> {
				doc.setUserName(userRepository.getUserNameByUseId(doc.getUserId()));
				
				Document document = documentRepository.getById(doc.getDocument().getDocumentId());
				
				//doc.setDocumentHeadApproval(document.getDocumentApproval());
				
				Category category = categoryRepository.getById(document.getDocumentCategory());
				int departmentId = category.getDepartmentId();			
				String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
				
				document.setCategoryName(category.getCategoryName());
				document.setDocumentTypeName(documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName());
				if(document.getDocumentSubType() != 0) {
					document.setDocumentSubTypeName(documentSubTypeRepository.getById(document.getDocumentSubType()).getDocumentSubTypeName());
				} else {
					document.setDocumentSubTypeName("-");
				}
				document.setDepartmentName(departmentName);
								
				Project project = projectRepository.getById(document.getProjectId());
				document.setProjectName(project.getProjectName());				
			});
			
			model.addAttribute("documents",documentRevisedList);
			
			return "timeline";
	 }	
		
	 
	 @GetMapping("/add-document/{projectId}") 
     public String add_document(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int projectId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
									
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
						
			List<Category> categoryList = user.getRole().equals("ROLE_ADMIN")? categoryRepository.findAll() : categoryRepository.getCategoryByDepartmentId(user.getDepartmentId());
			
			model.addAttribute("departmentList", departmentRepository.findAll());
			model.addAttribute("categoryList", categoryList);
			
			model.addAttribute("project", projectRepository.getById(projectId));
			model.addAttribute("projectId", projectId);
			
			return "add-document";
	 }
	 
	 @PostMapping("/save-document")
	 public String saveDocument(@ModelAttribute ActiveLinkMenu menu, @RequestParam("files") MultipartFile[] files, @ModelAttribute("document") Document document, 
	    		BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) throws Exception {
		 String sessionRoot = null;
		 try {
			    String userName = principal.getName();
				User user = userRepository.getUserByUserName(userName);
				float versionNumber = 1.0f;
				sessionRoot = createSessionTempRoot("document");
				
				StringBuffer upload_dir = new StringBuffer(sessionRoot).append(projectRepository.getById(document.getProjectId()).getProjectName()).append("/");
								
				String documentType = documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName();
				CreateFolder.newFolder(upload_dir.toString(), documentType);
				
				int documentSubTypeId = document.getDocumentSubType();
				if(documentSubTypeId != 0) {
					String documentSubType = documentSubTypeRepository.getById(documentSubTypeId).getDocumentSubTypeName();
					CreateFolder.newFolder((upload_dir).append(documentType).append("/").toString(), documentSubType);
					upload_dir.append(documentSubType).append("/");
				}else {
					upload_dir.append(documentType).append("/");
				}
				
				CreateFolder.newFolder(upload_dir.toString(), String.valueOf(versionNumber));
				upload_dir.append(versionNumber).append("/");
				String uploadDirPath = upload_dir.toString();
				String relativeUploadDir = toRelativeKey(uploadDirPath, sessionRoot);
								
				String barcode = String.valueOf(DateTimeUtil.getTimeStampInMiliseconds());
				boolean isBarcodeCreated = BarCodeImage.getBarCodeImage(barcode, uploadDirPath);
				
				String documentTypeSel = document.getDocumentTypeSelection();
				String documentName = uploadDirPath+document.getDocumentTitle()+versionNumber+".pdf";
				String barcodeFile  = uploadDirPath+barcode+".png";
				String pdfName = "";
				
				if("PDF".equals(documentTypeSel)) {
					MultipartFile file = files[0];
					String sourceName = getSafeFileName(file.getOriginalFilename());
					String baseName = sourceName.lastIndexOf(".") > 0 ? sourceName.substring(0, sourceName.lastIndexOf(".")) : sourceName;
					String fileName = baseName + versionNumber + ".pdf";
					File file1 = new  File(uploadDirPath+fileName);
					Files.write(file1.toPath(), file.getBytes());
					
					if(isBarcodeCreated) {
					   AddBarcodeToPDF.addBarcodeToPdf(uploadDirPath, fileName, barcodeFile);
					   pdfName = fileName;
					}
					 
				} else if("Images".equals(documentTypeSel)) {
					
					List<String> fileNames = new ArrayList<String>();
					for (MultipartFile file : Arrays.asList(files)) {
						String fileName = getSafeFileName(file.getOriginalFilename());
						File file1 = new  File(uploadDirPath+fileName);
						Files.write(file1.toPath(), file.getBytes());
						fileNames.add(uploadDirPath+fileName);
					}
				   				
					if(isBarcodeCreated) {
						CombineIntoPDF.combineImagesIntoPDF(documentName, fileNames.stream().toArray(String[]::new)); 
						pdfName = document.getDocumentTitle()+versionNumber+".pdf";
						AddBarcodeToPDF.addBarcodeToPdf(uploadDirPath, pdfName, barcodeFile);						
					}
				}

				if (!pdfName.isEmpty()) {
					s3StorageService.uploadLocalFile(relativeUploadDir + pdfName, new File(uploadDirPath + pdfName), "application/pdf");
				}
				if (isBarcodeCreated) {
					s3StorageService.uploadLocalFile(relativeUploadDir + barcode + ".png", new File(barcodeFile), "image/png");
				}
					
				document.setCreatedDatetime(DateTimeUtil.getSysDateTime());
				document.setUserId(user.getId()); 
				document.setDocumentName(pdfName); 
				document.setDocumentBarcode(barcode);
				document.setVersionNumber(versionNumber);
				document.setLastVersionNumber(versionNumber);
				document.setDocumentRevisedId(0);
				document.setDocumentApproval(0);
				document.setApprovalDatetime("");
				
				DocumentRevised documentRevised = new DocumentRevised();
				List<DocumentRevised> docRevisedList = new ArrayList<DocumentRevised>();
				documentRevised.setCreatedDatetime(DateTimeUtil.getSysDateTime());
				documentRevised.setUserId(user.getId()); 
				documentRevised.setDocumentName(pdfName); 				
				documentRevised.setVersionNumber(versionNumber);				
				documentRevised.setDocumentApproval(1);
				documentRevised.setApprovalDatetime("");
				documentRevised.setNote(document.getNote());
				documentRevised.setDocumentTypeSelection("PDF");
				documentRevised.setVersionTypeSelection("Major");
				documentRevised.setDocument(document);
				docRevisedList.add(documentRevised);
				
				document.setDocumentRevisedList(docRevisedList);
				documentRepository.save(document);
				
				redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
				return "redirect:/documents/"+document.getProjectId();
				
		 } catch (Exception e) {
				e.printStackTrace();
				redirectAttrs.addFlashAttribute("messageError", "Something went wrong. Please try again.");
				return "redirect:/documents/"+document.getProjectId();
		 } finally {
				cleanupTempDirectory(sessionRoot);
		 }		
	 }
	 
	 @GetMapping("/add-document-revised/{documentId}/{documentRevisedId}") 
     public String add_document_revised(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int documentId, @PathVariable int documentRevisedId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
									
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
			
			Document document = documentRepository.getById(documentId);
			
			Category category = categoryRepository.getById(document.getDocumentCategory());
			int departmentId = category.getDepartmentId();			
			String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
			
			document.setCategoryName(category.getCategoryName());
			document.setDocumentTypeName(documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName());
			if(document.getDocumentSubType() != 0) {
				document.setDocumentSubTypeName(documentSubTypeRepository.getById(document.getDocumentSubType()).getDocumentSubTypeName());
			} else {
				document.setDocumentSubTypeName("-");
			}
			document.setDepartmentName(departmentName);
			
			model.addAttribute("document", document);
			model.addAttribute("project", projectRepository.getById(document.getProjectId()));
			model.addAttribute("previousDocumentRevisedId", documentRevisedId);
			
			/*
			 * List<Category> categoryList = user.getRole().equals("ROLE_ADMIN")?
			 * categoryRepository.findAll() :
			 * categoryRepository.getCategoryByDepartmentId(user.getDepartmentId());
			 * 
			 * model.addAttribute("departmentList", departmentRepository.findAll());
			 * model.addAttribute("categoryList", categoryList);
			 */
			
			return "add-document-revised";
	 }
	 
	 
	 @PostMapping("/save-document-revised")
	 public String saveDocumentRevised(@ModelAttribute ActiveLinkMenu menu, @RequestParam("files") MultipartFile[] files, @ModelAttribute("documentRevised") DocumentRevised documentRevised, 
	    		BindingResult result, Model model, Principal principal, RedirectAttributes redirectAttrs) throws Exception {
			
		// int documentId = documentRevised.getDocumentId();
		 
		 int documentId = documentRevised.getDocument().getDocumentId();
		 Document doc = documentRepository.getById(documentId);
		 
		 String userName = principal.getName();
		 User user = userRepository.getUserByUserName(userName);
		 String sessionRoot = null;
		 try {
			   
				float versionNumber = documentRevised.getVersionNumber();
				sessionRoot = createSessionTempRoot("document-revised");
				
				StringBuffer upload_dir = new StringBuffer(sessionRoot).append(projectRepository.getById(doc.getProjectId()).getProjectName()).append("/");
								
				String documentType = documentTypeRepository.getById(doc.getDocumentType()).getDocumentTypeName();
				CreateFolder.newFolder(upload_dir.toString(), documentType);
				
				int documentSubTypeId = doc.getDocumentSubType();
				if(documentSubTypeId != 0) {				
					String documentSubType = documentSubTypeRepository.getById(doc.getDocumentSubType()).getDocumentSubTypeName();
					CreateFolder.newFolder((upload_dir).append(documentType).append("/").toString(), documentSubType);
					upload_dir.append(documentSubType).append("/");	
				}else {
					upload_dir.append(documentType).append("/");		
				}
				
				CreateFolder.newFolder(upload_dir.toString(), String.valueOf(versionNumber));
				upload_dir.append(versionNumber).append("/");
				String uploadDirPath = upload_dir.toString();
				String relativeUploadDir = toRelativeKey(uploadDirPath, sessionRoot);
								
				String barcode = doc.getDocumentBarcode();
				boolean isBarcodeCreated = BarCodeImage.getBarCodeImage(barcode, uploadDirPath);
				
				String documentTypeSel = documentRevised.getDocumentTypeSelection();
				String documentName = uploadDirPath+doc.getDocumentTitle()+versionNumber+".pdf";
				String barcodeFile  = uploadDirPath+barcode+".png";
				String pdfName = "";
				
				if("PDF".equals(documentTypeSel)) {
					     MultipartFile file = files[0];
					     String sourceName = getSafeFileName(file.getOriginalFilename());
					     String baseName = sourceName.lastIndexOf(".") > 0 ? sourceName.substring(0, sourceName.lastIndexOf(".")) : sourceName;
						String fileName = baseName+versionNumber+".pdf";
						File file1 = new  File(uploadDirPath+fileName);
						Files.write(file1.toPath(), file.getBytes());
						
						if(isBarcodeCreated) {
						   AddBarcodeToPDF.addBarcodeToPdf(uploadDirPath, fileName, barcodeFile);
						   pdfName = fileName;
						}
					 
				} else if("Images".equals(documentTypeSel)) {
					
					List<String> fileNames = new ArrayList<String>();
					for (MultipartFile file : Arrays.asList(files)) {
						String fileName = getSafeFileName(file.getOriginalFilename());
						File file1 = new  File(uploadDirPath+fileName);
						Files.write(file1.toPath(), file.getBytes());
						fileNames.add(uploadDirPath+fileName);
					}
				   				
					if(isBarcodeCreated) {
						CombineIntoPDF.combineImagesIntoPDF(documentName, fileNames.stream().toArray(String[]::new)); 
						pdfName = doc.getDocumentTitle()+versionNumber+".pdf";
						AddBarcodeToPDF.addBarcodeToPdf(uploadDirPath, pdfName, barcodeFile);						
					}
				}

				if (!pdfName.isEmpty()) {
					s3StorageService.uploadLocalFile(relativeUploadDir + pdfName, new File(uploadDirPath + pdfName), "application/pdf");
				}
				if (isBarcodeCreated) {
					s3StorageService.uploadLocalFile(relativeUploadDir + barcode + ".png", new File(barcodeFile), "image/png");
				}
					
				documentRevised.setCreatedDatetime(DateTimeUtil.getSysDateTime());
				documentRevised.setUserId(user.getId()); 
				documentRevised.setDocumentName(pdfName); 
				//documentRevised.setDocumentBarcode(barcode);
				documentRevised.setVersionNumber(versionNumber);				
				documentRevised.setDocumentApproval(1);
				documentRevised.setApprovalDatetime("");
				//documentRepository.save(document);
				documentRevisedRepository.save(documentRevised);
				
				documentRevisedRepository.updateRevisedDocumentApprovalToZero(documentRevised.getPreviousDocumentRevisedId());
				
				documentRepository.updateLatedVersionNumber(documentId, versionNumber);
				
				redirectAttrs.addFlashAttribute("messageSuccess", "Success.");
				return "redirect:/documents/"+doc.getProjectId();
				
		 } catch (Exception e) {
				e.printStackTrace();
				redirectAttrs.addFlashAttribute("messageError", "Something went wrong. Please try again.");
				return "redirect:/documents/"+doc.getProjectId();
		 } finally {
				cleanupTempDirectory(sessionRoot);
		 }		
	 }
	 
	 
	 @GetMapping("/view-document-details/{documentId}")
     public String view_document_details(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int documentId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			String url = getServerUrl();
									
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
						
			Document document = documentRepository.getById(documentId);	
			
			DocumentRevised docRevised = documentRevisedRepository.getLatestRevisedDocument(documentId);
					//documentRevisedRepository.findFirstByDocumentIdOrderByDocumentRevisedIdDesc(documentId);	
			//int docViewId = 0;
			//if(docRevised != null) {				
			    int docViewId = docRevised.getDocumentRevisedId();
				docRevised.setUserName(userRepository.getUserNameByUseId(docRevised.getUserId()));
				model.addAttribute("documentRevised", docRevised);				
			/*}else {
				model.addAttribute("documentRevised", new DocumentRevised());
			}*/	
									
			document.setUserName(userRepository.getUserNameByUseId(document.getUserId()));
			
			Category category = categoryRepository.getById(document.getDocumentCategory());
			int departmentId = category.getDepartmentId();			
			String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
			
			document.setCategoryName(category.getCategoryName());
			String documentTypeName = documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName();
			document.setDocumentTypeName(documentTypeName);
			
			String documentSubTypeName = "";
			if(document.getDocumentSubType() != 0) {
				documentSubTypeName = documentSubTypeRepository.getById(document.getDocumentSubType()).getDocumentSubTypeName();
				document.setDocumentSubTypeName(documentSubTypeName);
				documentSubTypeName = documentSubTypeName+"/";
			} else {
				document.setDocumentSubTypeName("-");
			}
			document.setDepartmentName(departmentName);
			
			model.addAttribute("document", document);
			model.addAttribute("documentId", documentId);
			
			Project project =  projectRepository.getById(document.getProjectId());
			model.addAttribute("project",project);
			model.addAttribute("projectId", document.getProjectId());
			
			model.addAttribute("filePath", url+project.getProjectName()+"/"+documentTypeName+"/"+documentSubTypeName+docRevised.getVersionNumber()+"/"); 
			model.addAttribute("contextPath", CONTEXT_URL);
			
			DocumentView docView = new DocumentView();
			docView.setDocumentId(documentId);
			docView.setDocumentRevisedId(docViewId);
			docView.setUserId(user.getId());
			docView.setViewDatetime(DateTimeUtil.getSysDateTime());
			documentViewRepository.save(docView);
			
			return "view-document-details";
	 }
	 
	 
	 @GetMapping("/view-document-version/{documentRevisedId}")
     public String viewDocumentVersion(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int documentRevisedId) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			String url = getServerUrl();
			
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			
			
			DocumentRevised docRevised = documentRevisedRepository.getById(documentRevisedId);			
			docRevised.setUserName(userRepository.getUserNameByUseId(docRevised.getUserId()));
			model.addAttribute("documentRevised", docRevised);				
						
			//int documentId = docRevised.getDocumentId();
			
			int documentId = docRevised.getDocument().getDocumentId();
			Document document = documentRepository.getById(documentId);							
			document.setUserName(userRepository.getUserNameByUseId(document.getUserId()));
			
			Category category = categoryRepository.getById(document.getDocumentCategory());
			int departmentId = category.getDepartmentId();			
			String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
			
			document.setCategoryName(category.getCategoryName());
			
			String documentTypeName = documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName();
			document.setDocumentTypeName(documentTypeName);
			
			String documentSubTypeName = "";
			if(document.getDocumentSubType() != 0) {
				documentSubTypeName = documentSubTypeRepository.getById(document.getDocumentSubType()).getDocumentSubTypeName();
				document.setDocumentSubTypeName(documentSubTypeName);
				documentSubTypeName = documentSubTypeName+"/";
			} else {
				document.setDocumentSubTypeName("-");
			}
			document.setDepartmentName(departmentName);
			
			model.addAttribute("document", document);
			model.addAttribute("documentId", documentId);
			
			Project project =  projectRepository.getById(document.getProjectId());
			model.addAttribute("project",project);
			model.addAttribute("projectId", document.getProjectId());
			
			model.addAttribute("filePath", url+project.getProjectName()+"/"+documentTypeName+"/"+documentSubTypeName+docRevised.getVersionNumber()+"/"); 
			model.addAttribute("contextPath", CONTEXT_URL);
			
			DocumentView docView = new DocumentView();
			docView.setDocumentId(documentId);
			docView.setDocumentRevisedId(docRevised.getDocumentRevisedId());
			docView.setUserId(user.getId());
			docView.setViewDatetime(DateTimeUtil.getSysDateTime());
			documentViewRepository.save(docView);
			
			return "view-document-version";
	 }
	 
	 @GetMapping("/document-history/{documentId}")
	 public String documentHistory(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @PathVariable int documentId) {
			
			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			model.addAttribute("user", user);
			menu.setDashboardLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("filePath", getServerUrl());		
			model.addAttribute("contextPath", CONTEXT_URL);
			model.addAttribute("documentId", documentId);
			
			
			Document document = documentRepository.getById(documentId);			
			document.setUserName(userRepository.getUserNameByUseId(document.getUserId()));
			
			Category category = categoryRepository.getById(document.getDocumentCategory());
			int departmentId = category.getDepartmentId();			
			String departmentName = departmentRepository.getById(departmentId).getDepartmentName();
			
			document.setCategoryName(category.getCategoryName());
			document.setDocumentTypeName(documentTypeRepository.getById(document.getDocumentType()).getDocumentTypeName());
			if(document.getDocumentSubType() != 0) {
				document.setDocumentSubTypeName(documentSubTypeRepository.getById(document.getDocumentSubType()).getDocumentSubTypeName());
			} else {
				document.setDocumentSubTypeName("-");
			}
			document.setDepartmentName(departmentName);
			document.setUserName(userRepository.getUserNameByUseId(document.getUserId()));
			
			model.addAttribute("document", document);
			
			List<DocumentRevised> documentList = documentRevisedRepository.getRevisedDocumentByDocumentId(documentId);
			documentList.forEach(doc -> {
						doc.setUserName(userRepository.getUserNameByUseId(document.getUserId()));
			});
			
			model.addAttribute("documents",documentList);
			model.addAttribute("project", projectRepository.getById(document.getProjectId()));
			model.addAttribute("projectId", document.getProjectId());
			
			return "document-history";
	 }	
	 
	 @GetMapping("/pdf")
	 public String showPDF(Model model) {
	     try {
	         String pdfFilePath = "D://PICTURES/My docs.pdf";
	         PDDocument document = PDDocument.load(new File(pdfFilePath));
	         PDFRenderer pdfRenderer = new PDFRenderer(document);

	         int pageCount = document.getNumberOfPages();
	         String[] base64Images = new String[pageCount];

	         for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
	             BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
	             base64Images[pageIndex] = convertImageToBase64(image);
	         }

	         document.close();

	         model.addAttribute("pageCount", pageCount);
	         model.addAttribute("base64Images", base64Images);
	         model.addAttribute("imageUtils", new ImageUtils()); // Add the utility class instance to the model

	     } catch (IOException e) {
	         e.printStackTrace();
	     }

	     return "pdf-viewer";
	 }

	 private String convertImageToBase64(BufferedImage image) {
	     try {
	         ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         ImageIO.write(image, "png", baos);
	         byte[] imageBytes = baos.toByteArray();
	         return java.util.Base64.getEncoder().encodeToString(imageBytes);
	     } catch (IOException e) {
	         e.printStackTrace();
	         return "";
	     }
	 }
	 
	 @PostMapping("/addDepartment")
	 public @ResponseBody List<Department> addDepartment(@RequestParam String departmentName, Model model, Principal principal) {

			Department ctgr = departmentRepository.getDepartmentByDepartmentName(departmentName);
			
			if(ctgr == null) {
				 Department department = new Department();
				 department.setDepartmentName(departmentName);
				
				 String userName = principal.getName();
				 User user = userRepository.getUserByUserName(userName);
			     int userId = user.getId();
			
			     department.setUserId(userId);
			     department.setCreatedDatetime(DateTimeUtil.getSysDateTime());
				
				departmentRepository.save(department);
			}
			
			List<Department> departmentList = departmentRepository.findAll();
						
			return departmentList;
	 }
	 
	 @PostMapping("/addCategory")
	 public @ResponseBody List<Category> addCategory(@RequestParam String categoryName, @RequestParam int departmentId,  Model model, Principal principal) {

			Category ctgr = categoryRepository.getCategoryByCategoryName(categoryName);
			
			if(ctgr == null) {
				Category category = new Category();
				category.setDepartmentId(departmentId);
				category.setCreatedDatetime(DateTimeUtil.getSysDateTime());
				
				String userName = principal.getName();
				User user = userRepository.getUserByUserName(userName);
				category.setUserId(user.getId()); 
				
				category.setCategoryName(categoryName);
				
				categoryRepository.save(category);
			}
			
			List<Category> categoryList = categoryRepository.findAll();
						
			return categoryList;
	 }
	 
	  	 
	 @PostMapping("/getDocumentTypeList")
	 public @ResponseBody List<DocumentType> getDocumentTypeList(@RequestParam int categoryName, Model model) {
			//System.out.println("getDocumentTypeList: categoryName "+categoryName);
			List<DocumentType> documentTypeList = documentTypeRepository.getDocumentTypeListByCategoryName(categoryName);
			//System.out.println("getDocumentTypeList: "+documentTypeList);
			return documentTypeList;
	 }
	 
	 @PostMapping("/addDocumentType")
	 public @ResponseBody List<DocumentType> addDocumentType(@RequestParam int categoryName, @RequestParam String documentTypeName, Model model) {
			
			DocumentType docType = documentTypeRepository.getDocumentTypeByDocumentTypeName(documentTypeName);				
			if(docType == null) {
				DocumentType sc = new DocumentType();
				sc.setCategoryName(categoryName);
				sc.setDocumentTypeName(documentTypeName);
				
				documentTypeRepository.save(sc);
			}			
			List<DocumentType> documentTypeList = documentTypeRepository.findDocumentTypeByCategoryName(categoryName);
								
			return documentTypeList;
	 }
	 
	 @GetMapping("/getDocumentSubTypeList")
	 public @ResponseBody List<DocumentSubType> getDocumentSubTypeList(@RequestParam int documentTypeName, Model model) {
			
			List<DocumentSubType> documentSubTypeList = documentSubTypeRepository.getDocumentSubTypeListByDocumentTypeName(documentTypeName);
			
			return documentSubTypeList;
	 }
	 
	 @PostMapping("/addDocumentSubType")
	 public @ResponseBody List<DocumentSubType> addDocumentSubType(@RequestParam int categoryTypeName, @RequestParam int documentTypeName, @RequestParam String documentSubTypeName, Model model) {
			
		    DocumentSubType docType = documentSubTypeRepository.getDocumentSubTypeByDocumentTypeName(documentSubTypeName);				
			if(docType == null) {
				DocumentSubType sc = new DocumentSubType();
				sc.setCategoryName(categoryTypeName);
				sc.setDocumentTypeName(documentTypeName);
				sc.setDocumentSubTypeName(documentSubTypeName);
				
				documentSubTypeRepository.save(sc);
			}			
			List<DocumentSubType> documentSubTypeList = documentSubTypeRepository.findDocumentSubTypeByDocumentTypeName(documentTypeName);
								
			return documentSubTypeList;
	 }
	
	 @PostMapping("/approveDocument")
	 public @ResponseBody boolean approveDocument(@RequestParam int documentId, @RequestParam int documentRevisedId, @RequestParam  int userId) {
	       
		    try {		    
		    	documentRepository.updateDocumentApproval(documentId, 1, documentRevisedId, DateTimeUtil.getSysDateTime(), userId); 
		    } catch (Exception e) {
	        	e.printStackTrace();
	        	return false;
	        }
		    return true;
	 }
	   
	 
	 @GetMapping("/document-view-report") 
     public String documentViewReport(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal) {
		 
		    String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
									
			model.addAttribute("user", user);
			menu.setDocumentViewReportLink("active");			
			model.addAttribute("menu", menu);
			model.addAttribute("userList", userRepository.findAll());
			model.addAttribute("projectList", projectRepository.findAll());			
			model.addAttribute("filePath", getServerUrl());
			model.addAttribute("contextPath", CONTEXT_URL);
						
			return "document-view-report";
	 }
	 
	 @GetMapping("/getDocumentListByProject")
	 public @ResponseBody  List<DocumentData>  getDocumentListByProject(@RequestParam int projectId, Model model) {
			
			List<DocumentData> documentList = null;
			try {
				documentList = documentRepository.getDocumentDataByProjectId(projectId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		    return documentList;
	 }
	 
	 @GetMapping("/document-view-report-result") 
     public String documentViewReportResult(@ModelAttribute ActiveLinkMenu menu, Model model, Principal principal, @RequestParam int documentId, @RequestParam int userId) {
		// System.out.println("document view report "+documentId+" "+userId);
		 try {
			 
			 String userName = principal.getName();
				User user = userRepository.getUserByUserName(userName);
										
				model.addAttribute("user", user);
				menu.setDocumentViewReportLink("active");			
				model.addAttribute("menu", menu);
				model.addAttribute("userList", userRepository.findAll());
				model.addAttribute("projectList", projectRepository.findAll());			
				model.addAttribute("filePath", getServerUrl());
				model.addAttribute("contextPath", CONTEXT_URL);
							
				List<DocumentViewReport> documentViewList = null;
				
				if(userId == 0) {
					documentViewList = documentViewRepository.getDocumentViewReportByDocumentId(documentId);
				}else if(userId > 0) {
					documentViewList = documentViewRepository.getDocumentViewReportByDocumentIdAndUserId(documentId, userId);
				}
						
				//System.out.println(documentViewList);
											
				model.addAttribute("documentViewList", documentViewList);	
				model.addAttribute("docTitle", documentRepository.getById(documentId).getDocumentTitle());	
			 
		 } catch(Exception e) {
			   e.printStackTrace();
		 }
		    
						
			return "document-view-report";
	 }

	 private String getServerUrl() {
		return s3StorageService.buildPublicBaseUrl();
	 }

	 private String createSessionTempRoot(String scope) throws IOException {
		Path basePath = Paths.get(tempStorageRoot);
		Files.createDirectories(basePath);
		Path sessionPath = basePath.resolve(scope + "-" + UUID.randomUUID().toString());
		Files.createDirectories(sessionPath);
		return ensureTrailingSlash(sessionPath.toString().replace("\\", "/"));
	 }

	 private String toRelativeKey(String path, String rootPath) {
		String normalizedPath = path == null ? "" : path.replace("\\", "/");
		String normalizedRoot = ensureTrailingSlash(rootPath == null ? "" : rootPath.replace("\\", "/"));
		if (normalizedPath.startsWith(normalizedRoot)) {
			return normalizedPath.substring(normalizedRoot.length());
		}
		return normalizedPath;
	 }

	 private void cleanupTempDirectory(String rootPath) {
		if (rootPath == null || rootPath.trim().isEmpty()) {
			return;
		}
		Path path = Paths.get(rootPath);
		if (!Files.exists(path)) {
			return;
		}
		try {
			Files.walk(path)
					.sorted(Comparator.reverseOrder())
					.forEach(p -> {
						try {
							Files.deleteIfExists(p);
						} catch (IOException ex) {
							logger.warn("Failed to cleanup temp path: {}", p.toString());
						}
					});
		} catch (IOException ex) {
			logger.warn("Failed to walk temp directory: {}", rootPath);
		}
	 }

	 private String getSafeFileName(String originalFileName) {
		if (originalFileName == null || originalFileName.trim().isEmpty()) {
			return "file";
		}
		String normalized = originalFileName.replace("\\", "/");
		int lastSlash = normalized.lastIndexOf("/");
		if (lastSlash >= 0) {
			return normalized.substring(lastSlash + 1);
		}
		return normalized;
	 }

	 private String getFileExtension(String fileName) {
		if (fileName == null) {
			return "";
		}
		int idx = fileName.lastIndexOf(".");
		if (idx < 0) {
			return "";
		}
		return fileName.substring(idx);
	 }

	 private String ensureTrailingSlash(String value) {
		if (value.endsWith("/")) {
			return value;
		}
		return value + "/";
	 }
	 
}
