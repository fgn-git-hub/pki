// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2012 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---
package com.netscape.certsrv.system;

import javax.ws.rs.POST;
import javax.ws.rs.Path;


/**
 * @author alee
 */
@Path("installer")
public interface SystemConfigResource {

    @POST
    @Path("setupCert")
    public SystemCertData setupCert(CertificateSetupRequest request) throws Exception;

    @POST
    @Path("setupAdmin")
    public AdminSetupResponse setupAdmin(AdminSetupRequest request) throws Exception;

    @POST
    @Path("setupSecurityDomain")
    public void setupSecurityDomain(SecurityDomainSetupRequest request) throws Exception;

    @POST
    @Path("setupDatabaseUser")
    public void setupDatabaseUser(DatabaseUserSetupRequest request) throws Exception;

    @POST
    @Path("finalizeConfiguration")
    public void finalizeConfiguration(FinalizeConfigRequest request) throws Exception;
}
