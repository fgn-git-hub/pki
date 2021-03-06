project(ldapauth_library CXX)

set(LDAPAUTH_PUBLIC_INCLUDE_DIRS
  ${CMAKE_CURRENT_BINARY_DIR}
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${TPS_INCLUDE_DIR}
  CACHE INTERNAL "ldapauth public include directories"
)

set(LDAPAUTH_PRIVATE_INCLUDE_DIRS
  ${LDAPAUTH_PUBLIC_INCLUDE_DIRS}
  ${CMAKE_BINARY_DIR}
  ${NSPR_INCLUDE_DIRS}
  ${NSS_INCLUDE_DIRS}
  ${LDAP_INCLUDE_DIRS}
)

set(LDAPAUTH_SHARED_LIBRARY
  ldapauth_library
  CACHE INTERNAL "ldapauth shared library"
)

set(LDAPAUTH_LINK_LIBRARIES
  ${NSPR_LIBRARIES}
  ${NSS_LIBRARIES}
  ${LDAP_LIBRARIES}
  ${TOKENDB_SHARED_LIBRARY}
  ${TPS_SHARED_LIBRARY}
)

set(ldapauth_library_SRCS
    LDAP_Authentication.cpp
)

include_directories(${LDAPAUTH_PRIVATE_INCLUDE_DIRS})

add_library(${LDAPAUTH_SHARED_LIBRARY} SHARED ${ldapauth_library_SRCS})
add_dependencies(${LDAPAUTH_SHARED_LIBRARY} pki-tps-jar)
target_link_libraries(${LDAPAUTH_SHARED_LIBRARY} ${LDAPAUTH_LINK_LIBRARIES})

set_target_properties(${LDAPAUTH_SHARED_LIBRARY}
    PROPERTIES
        OUTPUT_NAME
            ldapauth
)
