<%@ page session="false"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">

<jsp:include page="../fragments/header.jsp" />
<div class="content_body" style="margin-top: 156px">
	<div class="container">

		<c:if test="${not empty msg}">
			<div class="alert alert-${css} alert-dismissible" role="alert">
				<button type="button" class="close" data-dismiss="alert"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<strong>${msg}</strong>
			</div>
		</c:if>

		<h1>Donation Detail</h1>
		<br />

		<div class="row">
			<label class="col-sm-2">ID</label>
			<div class="col-sm-10">${donation.id}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Donor ID</label>
			<div class="col-sm-10">${donation.donor}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Item Description</label>
			<div class="col-sm-10">${donation.description}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Scheduled Date</label>
			<div class="col-sm-10">${donation.scheduledDate}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Completed Date</label>
			<div class="col-sm-10">
				<c:choose>
					<c:when test="empty ${donation.completedDate}">
						Incomplete
					</c:when>
					<c:otherwise>
						${donation.completedDate}
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="row">
			<label class="col-sm-2">Address</label>
			<div class="col-sm-10">${donation.address} ${donation.city}, ${donation.province}, ${donation.postalCode}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Drop Fee</label>
			<div class="col-sm-10">${donation.dropFee}</div>
		</div>

		<div class="row">
			<label class="col-sm-2">Tacking</label>
			<div class="col-sm-10">
				<c:choose>
					<c:when test="empty ${donation.tacking}">
						N/A
					</c:when>
					<c:otherwise>
						${donation.tacking}
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="row">
			<label class="col-sm-2">Tax Receipts?</label>
			<div class="col-sm-10">${donation.receipts}</div>
		</div>
		
		<spring:url value="/donations" var="donationList" />
		<button class="btn btn-info" onclick="location.href='${donationList}'">Donations</button>

	</div>
</div>

<jsp:include page="../fragments/footer.jsp" />

</body>
</html>