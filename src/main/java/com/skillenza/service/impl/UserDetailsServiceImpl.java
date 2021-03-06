/**
 * 
 */
package com.skillenza.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillenza.model.CarDetails;
import com.skillenza.model.UserDetails;
import com.skillenza.repository.CustomUserRepository;
import com.skillenza.repository.UserDetailsRepository;
import com.skillenza.request.CarDetailsForm;
import com.skillenza.request.UserDetailsForm;
import com.skillenza.service.CarDetailsService;
import com.skillenza.service.UserDetailsService;
import com.skillenza.utility.SessionUtil;
import com.skillenza.validation.UserCredentialsVlaidation;

/**
 * @author Mayank
 *
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserDetailsRepository userDetailsRepository;

	@Autowired
	CustomUserRepository customUserRepository;

	@Autowired
	CarDetailsService carDetailsService;

	@Override
	public boolean isExist(long id) {
		return userDetailsRepository.existsById(id);
	}

	@Override
	public Optional<UserDetails> findById(long id) {
		Optional<UserDetails> userDetails = userDetailsRepository.findById(id);
		return userDetails;
	}

	@Override
	public Map<String, UserDetails> save(UserDetailsForm userDetailsForm) {
		Map<String, UserDetails> userResponseMap = new HashMap<String, UserDetails>();
		UserDetails userDetails = null;
		boolean result = false;
		if (userDetailsForm != null) {
			if (userDetailsForm.getUserName() != null)
				result = UserCredentialsVlaidation.isValidMail(userDetailsForm.getUserName());
			if (result == false) {
				userResponseMap.put("Invalid Email", userDetails);
				return userResponseMap;
			}
			userDetails = findUserByMailId(userDetailsForm.getUserName());
			if (userDetails != null) {
				userDetails = null;
				userResponseMap.put("Duplicate Email", userDetails);
				return userResponseMap;
			} else {
				if (userDetailsForm.getPassword() != null && userDetailsForm.getPassword().length() < 5) {
					userResponseMap.put("Invalid password, password should contain at least 5 characters", userDetails);
					return userResponseMap;
				}
				if (userDetailsForm.getContactNo() != null && !(userDetailsForm.getContactNo().length() == 10)) {
					try {
						char[] chArray = userDetailsForm.getContactNo().toCharArray();
						for (int i = 0; i <= chArray.length; i++) {
							if (!(Integer.valueOf(chArray[i]) instanceof Integer)) {
								userResponseMap.put(
										"Invalid contact number, contact number should contain 10 digit without country code and sepical characters ",
										userDetails);
								return userResponseMap;
							}
						}
					} catch (Exception exp) {
						userResponseMap.put(
								"Invalid contact number, contact number should contain 10 digit without country code and sepical characters ",
								userDetails);
						return userResponseMap;
					}
				}
				if (userDetails == null) {
					userDetails = new UserDetails();
				}
				userDetails.setFirstName(userDetailsForm.getFirstName());
				userDetails.setLastName(userDetailsForm.getLastName());
				userDetails.setPassword(userDetailsForm.getPassword());
				userDetails.setUserName(userDetailsForm.getUserName());
				userDetails.setAddress(userDetailsForm.getAddress());
				userDetails.setContactNo(userDetailsForm.getContactNo());
			}
		}
		userDetails = userDetailsRepository.save(userDetails);
		if (userDetails != null) {
			userResponseMap.put("Regestration Sucessfully", userDetails);
			return userResponseMap;
		}
		return userResponseMap;
	}

	@Override
	public Map<String, List<UserDetails>> saveListOfUsers(List<UserDetailsForm> userDetailsFormList) {
		Map<String, List<UserDetails>> mapResponseList = new HashMap<>();
		if (userDetailsFormList != null) {
			for (UserDetailsForm userDetailsForm : userDetailsFormList) {

				Map<String, UserDetails> mapResponse1 = save(userDetailsForm);
				String key = mapResponse1.keySet().iterator().next();
				List<UserDetails> userDetailsList = mapResponseList.get(key);
				if (userDetailsList == null) {
					userDetailsList = new ArrayList<>();
				}
				userDetailsList.add(mapResponse1.get(key));
				mapResponseList.put(key, userDetailsList);
			}
		}
		return mapResponseList;
	}

	@Override
	public List<UserDetails> findAll() {
		Iterable<UserDetails> userDetailsList = userDetailsRepository.findAll();
		return (List<UserDetails>) userDetailsList;
	}

	@Override
	public UserDetails findUserByMailId(String mailId) {

		return customUserRepository.findUserByMailId(mailId);
	}

	@Override
	public Map<String, UserDetails> loginUser(String mailId, String password) {
		boolean result = false;
		UserDetails userDetails = null;
		Map<String, UserDetails> userResponseMap = new HashMap<>();
		result = UserCredentialsVlaidation.isValidMail(mailId);
		if (result == false) {
			userResponseMap.put("Invalid Email", userDetails);
		} else if (password != null && password.length() < 5) {
			userResponseMap.put("Please Enter Correct email and password", userDetails);
		} else {
			userDetails = findUserByMailId(mailId);
			if (userDetails == null) {
				userResponseMap.put("Please Enter Correct email and password", userDetails);
			} else {
				if (userDetails.getPassword() != null && userDetails.getPassword().equals(password)) {
					userResponseMap.put("Login Successfully", userDetails);
				} else {
					userDetails = null;
					userResponseMap.put("Please Enter Correct email and password", userDetails);
				}
			}
		}
		return userResponseMap;
	}

	@Override
	public void bed(CarDetailsForm carDetailsForm) {
		Optional<CarDetails> carDetails = carDetailsService.getCarById(carDetailsForm.getId());
		CarDetails carDetailsNew = null;
		long userId = SessionUtil.getLoggedInUserId();
		Optional<UserDetails> userDetails = null;
		if (userId > 0) {
			userDetails = findById(userId);
		}
		if (carDetails != null) {
			if (carDetails.get().getMaxbidPrice() < carDetailsForm.getBidPrice()) {
				if (carDetailsNew == null) {
					carDetailsNew = new CarDetails();
				}
				carDetailsNew.setCarId(carDetailsForm.getId());
				carDetailsNew.setCarModel(carDetailsForm.getCarModel());
				carDetailsNew.setMaxbidPrice(carDetailsForm.getBidPrice());
				carDetailsNew.setUserDetails(userDetails.get());
			} else {
				// do nothing
			}
		}
	}
}
