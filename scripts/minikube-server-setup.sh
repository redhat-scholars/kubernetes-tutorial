declare MINIKUBE_PROFILE=${1:-devnation}
declare MINIKUBE_IP=${2:-$(hostname -I | awk '{print $1}')}
declare MINIKUBE_MEM=${3:-4096}
declare MINIKUBE_CPU=${4:-2}

#minikube start --memory=${MINIKUBE_MEM} --cpus=${MINIKUBE_CPU} --driver=kvm2 -p ${MINIKUBE_PROFILE} --apiserver-ips=${MINIKUBE_IP}

#MacOS
minikube start --memory=${MINIKUBE_MEM} --cpus=${MINIKUBE_CPU} --driver=virtualbox -p ${MINIKUBE_PROFILE} --apiserver-ips=${MINIKUBE_IP}


minikube config set profile ${MINIKUBE_PROFILE}

IPTABLE_RULES=('LIBVIRT_FWI' 'LIBVIRT_FWO')
for RULE in "${IPTABLE_RULES[@]}"; do
    declare RULE_INDEX=$(sudo iptables -L ${RULE} --line-numbers 2>/dev/null | grep REJECT | awk '{print $1}' | head -n 1)
    while [[ -n "${RULE_INDEX}" ]]; do
        echo "Deleting from rule: ${RULE} index: ${RULE_INDEX}> $(sudo iptables -L ${RULE} ${RULE_INDEX})"
        sudo iptables -D ${RULE} ${RULE_INDEX}
        # see if there are any other reject indeces
        RULE_INDEX=$(sudo iptables -L ${RULE} --line-numbers 2>/dev/null | grep REJECT | awk '{print $1}' | head -n 1)
        # echo "New Rule Index is $RULE_INDEX"
    done
done

# turn on DNAT
sudo iptables -t nat -A PREROUTING -p tcp --dport 30000:32767 -j DNAT --to-destination $(minikube ip):30000-32767
sudo iptables -t nat -A PREROUTING -p tcp --dport 8443 -j DNAT --to-destination $(minikube ip):8443

# This can be used to restore errors in the above script removing rules
# sudo iptables -A LIBVIRT_FWI -d 192.168.122.0/24 -o virbr0 -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT
# sudo iptables -A LIBVIRT_FWO -s 192.168.122.0/24 -i virbr0 -j ACCEPT

# turn off firewall
sudo systemctl disable firewalld;

