declare MINIKUBE_PROFILE=${1:-devnation}

minikube config set profile ${MINIKUBE_PROFILE}

#remove the reject rules turn into a loop
iptables -L LIBVIRT_FWI --line-numbers | grep REJECT | awk '{print $1}' | head -n 1
iptables -D LIBVIRT_FWO $(iptables -L LIBVIRT_FWO --line-numbers | grep REJECT | awk '{print $1}' | head -n 1)

# turn off firewall
sudo systemctl disable firewalld;

# turn on DNAT
sudo iptables -t nat -A PREROUTING -p tcp --dport 30000:32767 -j DNAT --to-destination $(minikube ip):30000-32767
sudo iptables -t nat -A PREROUTING -p tcp --dport 8443 -j DNAT --to-destination $(minikube ip):8443